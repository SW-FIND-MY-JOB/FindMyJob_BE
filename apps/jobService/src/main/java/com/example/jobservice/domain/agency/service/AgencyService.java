package com.example.jobservice.domain.agency.service;

import com.example.jobservice.domain.agency.converter.AgencyConverter;
import com.example.jobservice.domain.agency.dto.AgencyReqDTO;
import com.example.jobservice.domain.agency.dto.AgencyResDTO;
import com.example.jobservice.domain.agency.entity.Agency;
import com.example.jobservice.domain.agency.exception.status.AgencyErrorStatus;
import com.example.jobservice.domain.agency.repository.AgencyRepository;
import com.example.jobservice.global.exception.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgencyService {
    private final AgencyRepository agencyRepository;

    //기관 정보 가져오기
    public AgencyResDTO.AgencyInformDTO getAgencyInform(String agencyName){

        //기관명으로 정보 가져옴
        Agency agency = agencyRepository.findByInstNm(agencyName)
                .orElseThrow(()-> new GeneralException(AgencyErrorStatus._NOT_EXIST_AGENCY));

        return AgencyConverter.toAgencyResDTO(agency);
    }

    //모든 기관 정보 가져오기
    public List<AgencyResDTO.AgencyInformDTO> getAllAgencyInform(){

        //기관명으로 정보 가져옴
        List<Agency> agencyList = agencyRepository.findAll();

        return agencyList.stream()
                .map(AgencyConverter::toAgencyResDTO)
                .collect(Collectors.toList());
    }

    //기관 정보 저장하기
    @Transactional
    public void saveAgencyInform (List<AgencyReqDTO.AgencyInformDTO> agencyInformDTOList){
        List<Agency> agencies = agencyInformDTOList.stream()
                .map(dto -> Agency.builder()
                        .instNm(dto.getInstNm())
                        .ncsCdNmLst(dto.getNcsCdNmLst())
                        .logoUrl(dto.getLogoUrl())
                        .establishmentAt(dto.getEstablishmentAt())
                        .reason(dto.getReason())
                        .role(dto.getRole())
                        .captain(dto.getCaptain())
                        .city(dto.getCity())
                        .address(dto.getAddress())
                        .homepageUrl(dto.getHomepageUrl())
                        .build())
                .toList();

        agencyRepository.saveAll(agencies);
    }
    
    //기관정보 업데이트 하기
    @Transactional
    public void updateAgencyInform(String name, AgencyReqDTO.AgencyInformDTO agencyInformDTO){
        //기관명으로 가져오기
        Agency agency = agencyRepository.findByInstNm(name)
                .orElseThrow(()-> new GeneralException(AgencyErrorStatus._NOT_EXIST_AGENCY));

        //기관 유형 수정
        if (agencyInformDTO.getNcsCdNmLst() != null){
            agency.setNcsCdNmLst(agencyInformDTO.getNcsCdNmLst());
        }

        //로고 수정
        if (agencyInformDTO.getLogoUrl() != null){
            agency.setLogoUrl(agencyInformDTO.getLogoUrl());
        }

        //설립일 수정
        if (agencyInformDTO.getEstablishmentAt() != null){
            agency.setEstablishmentAt(agencyInformDTO.getEstablishmentAt());
        }

        //설립목적 수정
        if (agencyInformDTO.getReason() != null){
            agency.setReason(agencyInformDTO.getReason());
        }

        //역할 수정
        if (agencyInformDTO.getRole() != null){
            agency.setRole(agencyInformDTO.getRole());
        }

        //기관장 수정
        if (agencyInformDTO.getCaptain() != null){
            agency.setCaptain(agencyInformDTO.getCaptain());
        }

        //도시명 수정
        if (agencyInformDTO.getCity() != null){
            agency.setCity(agencyInformDTO.getCity());
        }

        //상세주소
        if (agencyInformDTO.getAddress() != null){
            agency.setAddress(agencyInformDTO.getAddress());
        }

        //홈페이지 주소
        if (agencyInformDTO.getHomepageUrl() != null){
            agency.setHomepageUrl(agencyInformDTO.getHomepageUrl());
        }

        agencyRepository.save(agency);
    }

    // 기관 정보 삭제하기
    @Transactional
    public void deleteAgencyInform(String name){
        if(agencyRepository.existsByInstNm(name)){
            agencyRepository.deleteByInstNm(name);
        }
    }
}
