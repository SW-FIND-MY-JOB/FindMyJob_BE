package com.example.jobservice.domain.agency.entity;

import com.example.jobservice.domain.common.BaseEntity;
import com.example.jobservice.domain.notice.entity.Notice;
import com.example.jobservice.domain.notice.entity.NoticeScrap;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Agency extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //기관명
    @Column(name = "inst_nm", nullable = false, unique = true, length = 50)
    private String instNm;

    //기관유형
    @Column(name = "ncs_cd_nm_lst", nullable = false, length = 50)
    private String ncsCdNmLst;

    //로고
    @Column(name = "logo_url")
    private String logoUrl;

    //설립일
    @Column(name = "establishment_at", length = 50)
    private String establishmentAt;

    //설립목적
    @Column(name = "reason")
    private String reason;

    //역할
    @Column(name = "role")
    private String role;

    //기관장
    @Column(name = "captain", length = 50)
    private String captain;

    //도시명
    @Column(name = "city", length = 50)
    private String city;

    //상세주소
    @Column(name = "address")
    private String address;

    //홈페이지 주소
    @Column(name = "homepage_url")
    private String homepageUrl;

    //채용 공고 정보와 매핑
    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL)
    private List<Notice> noticeList = new ArrayList<>();
}
