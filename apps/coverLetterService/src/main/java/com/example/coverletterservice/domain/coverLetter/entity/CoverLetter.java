package com.example.coverletterservice.domain.coverLetter.entity;

import com.example.coverletterservice.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CoverLetter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //기관명
    @Column(name = "inst_nm", nullable = false)
    private String instNm;

    //직무(보건)
    @Column(name = "ncs_cd_nm_lst", nullable = false)
    private String ncsCdNmLst;

    //질문
    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    //내용
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    //조회수
    @Setter
    @Column(name = "view_cnt")
    private Long viewCnt;

    //유저 id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "coverLetter", cascade = CascadeType.ALL)
    private List<CoverLetterScrap> scraps;
}
