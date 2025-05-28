package com.example.jobservice.domain.agency.exception.status;

import com.example.responselib.apiPayload.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AgencySuccessStatus implements BaseCode {
    //200
    _SUCCESS_GET_AGENCY_INFORM(HttpStatus.OK, "AGENCY2001", "기관 정보 가져오기 성공"),
    _SUCCESS_UPDATE_AGENCY_INFORM(HttpStatus.OK, "AGENCY2002", "기관 정보 업데이트 성공"),
    _SUCCESS_DELETE_AGENCY_INFORM(HttpStatus.OK, "AGENCY2003", "기관 정보 삭제 성공"),

    //201
    _SUCCESS_SAVE_AGENCY_INFORM(HttpStatus.CREATED, "AGENCY2011", "기관 정보 저장하기 성공"),

    
    _SUCCESS_DELETE_USER(HttpStatus.OK, "USER2003", "사용자 삭제 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
