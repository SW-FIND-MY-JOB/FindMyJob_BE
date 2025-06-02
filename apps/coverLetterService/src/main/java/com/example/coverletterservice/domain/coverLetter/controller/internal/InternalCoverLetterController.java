package com.example.coverletterservice.domain.coverLetter.controller.internal;

import com.example.coverletterservice.domain.coverLetter.service.CoverLetterService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/internal/cover-letter")
@RequiredArgsConstructor
public class InternalCoverLetterController {
    private final CoverLetterService coverLetterService;

    //자소서 내용 반환
    @GetMapping
    public ResponseEntity<String> getCoverLetterContent(@RequestParam @NotNull Long coverLetterId){
        String coverLetterContent = coverLetterService.getCoverLetterContent(coverLetterId);
        return ResponseEntity.ok(coverLetterContent);
    }
}
