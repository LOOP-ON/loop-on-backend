package com.loopon.routine.application.service;

import com.loopon.global.s3.S3Service;
import com.loopon.routine.application.dto.converter.RoutineConverter;
import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.routine.domain.Routine;
import com.loopon.routine.domain.RoutineProgress;
import com.loopon.routine.domain.service.RoutineCommandService;
import com.loopon.routine.infrastructure.RoutineJpaRepository;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.routine.infrastructure.RoutineProgressJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RoutineCommandServiceImpl implements RoutineCommandService {
    private final JourneyJpaRepository journeyRepository;
    private final RoutineJpaRepository routineRepository;
    private final RoutineProgressJpaRepository routineProgressRepository;
    private final S3Service s3Service;

    @Transactional
    @Override
    public RoutineResponse.PostRoutinesDto postRoutine(
            RoutineRequest.AddJRoutineDto routineRequest
    ){
        //여정 찾기
        Journey journey = journeyRepository.getReferenceById(routineRequest.journeyId());

        //루틴 객체 생성
        List<Routine> routines = routineRequest.routines().stream()
                .map(dto -> RoutineConverter.bodyToRoutine(journey, dto))
                .toList();

        //루틴 생성
        List<Routine> routineList = routineRepository.saveAll(routines);

        //루틴 Progress 3일치 생성
        LocalDate today = LocalDate.now();

        List<RoutineProgress> progressList =
                routineList.stream()
                        .flatMap(routine ->
                                Stream.of(
                                            today,
                                            today.plusDays(1),
                                            today.plusDays(2)
                                        )
                                        .map(date ->
                                                RoutineProgress.create(routine, date)
                                        )
                        )
                        .toList();

        routineProgressRepository.saveAll(progressList);

        //response 형태 맞추어 리턴
        return RoutineConverter.toPostRoutinesDto(routines);
    };

    @Transactional
    @Override
    public RoutineResponse.RoutineCertifyDto certifyRoutine(Long progressId, Long userId, MultipartFile image){
        //progress 조회
        RoutineProgress progress = routineProgressRepository
                .findById(progressId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        // 사용자의 루틴이 아닐 경우 : error -> 너무 깊어서
        if (!progress.getRoutine().getJourney().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("사용자의 루틴이 아닙니다.");
        }

        // s3에 이미지 업로드
        String imageUrl = s3Service.uploadFile(image);

        // imageUrl을 progress에 업로드 후 데이터 변경
        progress.certify(imageUrl);

        // 5. 응답 DTO 변환
        return RoutineConverter.toRoutineCertifyDto(progress);
    };

}
