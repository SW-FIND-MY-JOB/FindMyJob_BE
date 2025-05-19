package com.example.apigateway.global.exception;

import com.example.apigateway.global.apiPayLoad.ApiResponse;
import com.example.apigateway.global.apiPayLoad.dto.ErrorReasonDTO;
import com.example.apigateway.global.apiPayLoad.status.ErrorStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(-1)
@RequiredArgsConstructor
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiResponse<Object> response;

        if (ex instanceof GeneralException) {
            GeneralException ge = (GeneralException) ex;
            ErrorReasonDTO reason = ge.getErrorReasonHttpStatus();

            status = reason.getHttpStatus();
            log.error("사용자에러: {}", reason.getMessage());
            response = ApiResponse.onFailure(reason.getCode(), reason.getMessage(), null);
        } else {
            log.error("서버에러: {}", ex.getMessage());
            response = ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(), ErrorStatus._INTERNAL_SERVER_ERROR.getMessage(), null);
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            String json = objectMapper.writeValueAsString(response);
            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(json.getBytes(StandardCharsets.UTF_8));

            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return exchange.getResponse().setComplete();
        }
    }
}