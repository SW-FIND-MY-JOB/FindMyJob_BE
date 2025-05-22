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
        private Long recrutPblntSn;
        private String instNm;
        private String ncsCdNmLst;
        private String hireTypeNmLst;
        private String workRgnNmLst;
        private String recrutSeNm;
        private String prefCondCn;
        private String pbancBgngYmd;
        private String pbancEndYmd;
        private String recrutPbancTtl;
        private String srcUrl;
        private String aplyQlfcCn;
        private String disqlfcRsn;
        private String scrnprcdrMthdExpln;
        private String acbgCondNmLst;
        private String nonatchRsn;
    }
}
