package com.example.jobservice.domain.notice.entity;

import com.example.jobservice.domain.agency.entity.Agency;
import com.example.jobservice.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends BaseEntity {
    //공고번호
    @Id
    private Long id;

    //기관명
    @Column(name = "inst_nm", nullable = false, length = 20)
    private String instNm;

    //기관유형명(직무)
    @Column(name = "ncs_cd_nm_lst", nullable = false)
    private String ncsCdNmLst;

    //채용 유형명 (정규직, 비정규직)
    @Column(name = "hire_type_nm_lst", nullable = false, length = 30)
    private String hireTypeNmLst;

    //근무지역
    @Column(name = "work_rgn_nm_lst", nullable = false)
    private String workRgnNmLst;

    //경력
    @Column(name = "recruit_se_nm", nullable = false, length = 30)
    private String recruitSeNm;

    //우대조건
    @Column(name = "pref_cond_cn", columnDefinition = "TEXT")
    private String prefCondCn;

    //공고시작일
    @Column(name = "pbanc_bgng_ymd", nullable = false)
    private LocalDate pbancBgngYmd;

    //공고마감일
    @Column(name = "pbanc_end_ymd", nullable = false)
    private LocalDate pbancEndYmd;

    //공고 타이틀
    @Column(name = "pbanc_ttl", nullable = false)
    private String recrutPbancTtl;

    //공고 사이트 주소
    @Column(name = "src_url")
    private String srcUrl;

    //지원조건
    @Column(name = "aply_qlfc_cn", columnDefinition = "TEXT")
    private String aplyQlfcCn;

    //제한조건
    @Column(name = "disqlfc_cn", columnDefinition = "TEXT")
    private String disqlfcRsn;

    //채용절차
    @Column(name = "hire_prcdr_mthd_expln", columnDefinition = "TEXT")
    private String scrnprcdrMthdExpln;

    //학력
    @Column(name = "acbg_cond_nm_lst", nullable = false)
    private String acbgCondNmLst;

    //지원방법
    @Column(name = "nonatch_rsn", columnDefinition = "TEXT")
    private String nonatchRsn;

    //기관 정보와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;

    //스크랩 한 정보와 매핑
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<NoticeScrap> noticeScrapList = new ArrayList<>();
}
