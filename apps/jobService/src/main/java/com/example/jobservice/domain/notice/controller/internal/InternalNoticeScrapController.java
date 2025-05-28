package com.example.jobservice.domain.notice.controller.internal;

import com.example.jobservice.domain.notice.service.NoticeScrapService;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/notice-scraps")
@RequiredArgsConstructor
public class InternalNoticeScrapController {
    private final NoticeScrapService noticeScrapService;

    //회원 탈퇴시 해당 유저id에 대한 정보 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Null> deleteUserNoticeScraps(@PathVariable Long userId){
        noticeScrapService.deleteUserNoticeScraps(userId);
        return ResponseEntity.ok().build();
    }
}
