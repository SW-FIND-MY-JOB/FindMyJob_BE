package com.example.authservice.domain.point.controller.api;

import com.example.authservice.domain.point.dto.PointResDTO;
import com.example.authservice.domain.point.exception.status.PointSuccessStatus;
import com.example.authservice.domain.point.service.PointService;
import com.example.responselib.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/points")
@Tag(name = "포인트 관련 API입니다", description = "포인트 관련 API입니다")
public class PointController {
    private final PointService pointService;

    //포인트 내역 조회
    @GetMapping
    @Operation(summary = "포인트 내역 가져오기", description = "포인트 내역 가져오기")
    public ApiResponse<Page<PointResDTO.pointInformDTO>> getPointHistory(HttpServletRequest request,
                                                                         @RequestParam(name = "page", defaultValue = "1") int page,
                                                                         @RequestParam(name = "size", defaultValue = "5") int size){

        Page<PointResDTO.pointInformDTO> pointInformDTOList = pointService.findAllByUser(request,page,size);
        return ApiResponse.of(PointSuccessStatus._SUCCESS_GET_USER_POINT, pointInformDTOList);
    }
}
