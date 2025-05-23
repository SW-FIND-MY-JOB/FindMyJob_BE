package com.example.jobservice.domain.notice.dto.notice;

import lombok.Data;

import java.util.List;

public class NoticeApiResponseDTO {
    @Data
    public static class NoticeApiResponse {
        private int resultCode;
        private String resultMsg;
        private int totalCount;
        private List<NoticePostingDTO> result;
    }

    @Data
    public static class NoticePostingDTO {
        //공고 번호
        private Long recrutPblntSn;
        //기관명
        private String instNm;
        //기관유형명
        private String ncsCdNmLst;
        //기관유형코드
        private String ncsCdLst;
        //채용유형명
        private String hireTypeNmLst;
        //채용유형코드
        private String hireTypeLst;
        //근무지역
        private String workRgnNmLst;
        //근무지역 코드
        private String workRgnLst;
        //경력
        private String recrutSeNm;
        //경력 코드
        private String recrutSe;
        //우대조건
        private String prefCondCn;
        //공고시작일
        private String pbancBgngYmd;
        //공고마감일
        private String pbancEndYmd;
        //공고 타이틀
        private String recrutPbancTtl;
        //공고 사이트 주소
        private String srcUrl;
        //지원조건
        private String aplyQlfcCn;
        //제한조건
        private String disqlfcRsn;
        //채용절차
        private String scrnprcdrMthdExpln;
        //학력
        private String acbgCondNmLst;
        //학력코드
        private String acbgCondLst;
        //지원방법
        private String nonatchRsn;
    }
}
