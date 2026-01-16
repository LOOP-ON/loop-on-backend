package com.loopon.challenge.application.converter;

import com.loopon.challenge.application.dto.command.HashtagAddCommand;
import com.loopon.challenge.application.dto.response.HashtagAddResponse;
import com.loopon.challenge.domain.Hashtag;

import java.util.ArrayList;
import java.util.List;

public class HashtagConverter {

    public static List<Hashtag> addHashtag(HashtagAddCommand dto) {
        List<Hashtag> resultList = new ArrayList<>();

        for (String hashtag : dto.hashtagList()) {
            resultList.add(Hashtag.builder()
                    .name(hashtag)
                    .build()
            );
        }

        return resultList;
    }


    public static HashtagAddResponse addHashtag(
            List<Hashtag> hashtagList
    ) {
        List<Long> hashtagIds = new ArrayList<>();
        for (Hashtag hashtag : hashtagList) {
            hashtagIds.add(hashtag.getId());
        }
        return HashtagAddResponse.builder()
                .hashtagIdList(hashtagIds)
                .build();
    }

}
