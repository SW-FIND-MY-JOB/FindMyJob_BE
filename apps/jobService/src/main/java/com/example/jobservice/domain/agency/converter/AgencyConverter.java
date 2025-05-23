package com.example.jobservice.domain.agency.converter;

import com.example.jobservice.domain.agency.dto.AgencyResDTO;
import com.example.jobservice.domain.agency.entity.Agency;

public class AgencyConverter {
    public static AgencyResDTO.AgencyInformDTO toAgencyResDTO(Agency agency) {
        return AgencyResDTO.AgencyInformDTO.builder()
                .instNm(agency.getInstNm())
                .ncsCdNmLst(agency.getNcsCdNmLst())
                .logoUrl(agency.getLogoUrl())
                .establishmentAt(agency.getEstablishmentAt())
                .reason(agency.getReason())
                .role(agency.getRole())
                .captain(agency.getCaptain())
                .city(agency.getCity())
                .address(agency.getAddress())
                .homepageUrl(agency.getHomepageUrl())
                .build();
    }
}
