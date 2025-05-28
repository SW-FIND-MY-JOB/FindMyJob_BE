package com.example.jobservice.domain.agency.repository;

import com.example.jobservice.domain.agency.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgencyRepository extends JpaRepository <Agency, Long> {
    //기관명으로 검색
    Optional<Agency> findByInstNm(String agencyName);

    //기관 존재 유무
    boolean existsByInstNm(String agencyName);

    //기관 제거
    void deleteByInstNm(String agencyName);
}
