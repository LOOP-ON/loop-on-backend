package com.loopon.challenge.application.service;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> uploadFiles(List<MultipartFile> files) {
        List<String> urlList = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                urlList.add(uploadFile(file));
            }
        }

        return urlList;
    }


    public String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFilename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(storedFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } catch (S3Exception | IOException e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        }

        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder()
                        .bucket(bucket)
                        .key(storedFileName)
                        .build())
                .toString();
    }
}