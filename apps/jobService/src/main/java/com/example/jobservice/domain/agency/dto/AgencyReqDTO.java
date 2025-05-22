package com.example.jobservice.domain.agency.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AgencyReqDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "기관 정보 요청 DTO")
    public static class AgencyInformDTO{
        @NotBlank
        @Schema(description = "기관명", example = "(사)남북교류협력지원협회")
        String instNm;

        @NotBlank
        @Schema(description = "기관유형", example = "기타공공기관")
        String ncsCdNmLst;

        @Schema(description = "로고주소", example = "https://www.kpu.ac.kr/img/logo/logo_kpu.png")
        String logoUrl;

        @Schema(description = "설립일", example = "20070518")
        String establishmentAt;

        @Schema(description = "설립목적", example = "정부 위탁업무 수행, 정책건의, 남북교류협력 관련 조사·연구 및 분석 등을 통한 남북교류협력 활성화 지원")
        String reason;

        @Schema(description = "역할", example = "o 남북교류협력과 관련한 정부 위탁사업 수행&cr;o 정부에 대한 남북교류협력 활성화 대책 건의&cr;o 남북교류협력과 관련한 조사 ·연구 및 분석&cr;o 남북교류협력 사업자간 정보공유 및 네트워크 구축&cr;o 기타 남북교류협력 촉진을 위한 사업 등")
        String role;

        @Schema(description = "기관장", example = "정낙근")
        String captain;

        @Schema(description = "도시명", example = "서울특별시")
        String city;

        @Schema(description = "상세주소", example = "서울특별시 중구 퇴계로 97, 601호")
        String address;

        @Schema(description = "홈페이지주소", example = "www.sonosa.or.kr")
        String homepageUrl;
    }
}
