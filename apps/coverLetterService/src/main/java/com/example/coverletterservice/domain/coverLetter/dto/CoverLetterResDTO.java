package com.example.coverletterservice.domain.coverLetter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CoverLetterResDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "자기소개서 단일 응답 DTO")
    public static class CoverLetterDetailInformDTO {
        @Schema(description = "자소서 id", example = "1")
        Long id;

        @Schema(description = "작성자 일치유무", example = "true")
        Boolean isAuthor;

        @Schema(description = "스크랩 유무", example = "true")
        Boolean isScrap;

        @Schema(description = "조회수", example = "100")
        Long viewCnt;

        @Schema(description = "기관명", example = "한국전력")
        String instNm;

        @Schema(description = "직무", example = "보건")
        String ncsCdNmLst;

        @Schema(description = "질문", example = "지원동기 (300자 이상)")
        String title;

        @Schema(description = "내용", example = "기후변화로 인해 탄소중립이 전 세계....")
        String content;

        @Schema(description = "작성일", example = "20220101")
        LocalDateTime createAt;

        @Schema(description = "최신 자소서 리스트")
        List<CoverLetterRecentInformDTO> recentInformList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "최신 자소서 응답 DTO")
    public static class CoverLetterRecentInformDTO{
        @Schema(description = "자소서 id", example = "1")
        Long id;

        @Schema(description = "기관명", example = "한국전력")
        String instNm;

        @Schema(description = "직무", example = "보건")
        String ncsCdNmLst;

        @Schema(description = "질문", example = "지원동기 (300자 이상)")
        String title;

        @Schema(description = "내용", example = "기후변화로 인해 탄소중립이 전 세계....")
        String content;
    }

    //자소서 조건 검색
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "자소서 응답 DTO")
    public static class CoverLetterInformDTO{
        @Schema(description = "자소서 id", example = "1")
        Long id;

        @Schema(description = "기관명", example = "한국전력")
        String instNm;

        @Schema(description = "직무", example = "보건")
        String ncsCdNmLst;

        @Schema(description = "질문", example = "지원동기 (300자 이상)")
        String title;

        @Schema(description = "내용", example = "기후변화로 인해 탄소중립이 전 세계....")
        String content;

        @Schema(description = "점수", example = "1000")
        Integer score;

        @Schema(description = "조회수", example = "123")
        Long viewCnt;

        @Schema(description = "자소서 스크랩 유무", example = "true")
        Boolean isScrap;
    }

    @Getter
    @Builder
    public static class CoverLetterIdResDTO{
        @Schema(description = "자소서 id", example = "1")
        Long id;

        @Schema(description = "자소서 점수", example = "1000")
        Integer score;

        @Schema(description = "자소서 적립 포인트", example = "1000")
        Integer point;

        @Schema(description = "자소서 상위 몇 %", example = "50.2")
        Integer percent;
    }

    @Getter
    @Builder
    public static class CoverLetterRankingResDTO{
        @Schema(description = "자소서 id", example = "1")
        Long id;

        @Schema(description = "자소서 점수", example = "1000")
        Integer score;

        @Schema(description = "랭킹", example = "1")
        Integer ranking;

        @Schema(description = "기관명", example = "한국전력")
        String instNm;

        @Schema(description = "직무", example = "보건")
        String ncsCdNmLst;

        @Schema(description = "질문", example = "지원동기 (300자 이상)")
        String title;

        @Schema(description = "작성자", example = "김땡땡")
        String writer;
    }
}
