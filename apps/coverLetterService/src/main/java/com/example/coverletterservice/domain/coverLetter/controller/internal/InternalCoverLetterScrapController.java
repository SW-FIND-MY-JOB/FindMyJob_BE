package com.example.coverletterservice.domain.coverLetter.controller.internal;

import com.example.coverletterservice.domain.coverLetter.service.CoverLetterScrapService;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/cover-letter-scraps")
@RequiredArgsConstructor
public class InternalCoverLetterScrapController {
    private final CoverLetterScrapService coverLetterScrapService;

    //회원 탈퇴시 해당 유저id에 대한 정보 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Null> deleteUserCoverLetterScrap(@PathVariable Long userId){
        coverLetterScrapService.deleteUserIdCoverLetterScrap(userId);
        return ResponseEntity.ok().build();
    }
}
