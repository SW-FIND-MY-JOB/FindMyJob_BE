package com.example.coverletterservice.domain.coverLetter.converter;

import com.example.coverletterservice.domain.coverLetter.entity.CoverLetter;
import com.example.coverletterservice.domain.coverLetter.entity.CoverLetterScrap;

public class CoverLetterScrapConverter {

    //객체로 변환
    public static CoverLetterScrap toCoverLetterScrap(CoverLetter coverLetter, Long userId){
        return CoverLetterScrap.builder()
                .userId(userId)
                .coverLetter(coverLetter)
                .build();
    }
}
