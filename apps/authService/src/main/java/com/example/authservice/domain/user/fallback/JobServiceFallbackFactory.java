package com.example.authservice.domain.user.fallback;

import com.example.authservice.domain.user.client.JobServiceClient;
import com.example.authservice.domain.user.exception.status.UserErrorStatus;
import com.example.authservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobServiceFallbackFactory implements FallbackFactory<JobServiceClient> {

    @Override
    public JobServiceClient create(Throwable cause) {
        return new JobServiceClient() {
            @Override
            public void deleteUserNoticeScraps(Long userId){
                log.error("사용자에 따른 채용정보 스크랩 정보 삭제 에러: {}", cause.getMessage());
                throw new GeneralException(ErrorStatus._JOB_SERVICE_UNAVAILABLE);
            }
        };
    }
}
