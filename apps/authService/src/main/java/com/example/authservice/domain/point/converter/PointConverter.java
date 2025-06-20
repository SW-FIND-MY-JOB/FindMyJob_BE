package com.example.authservice.domain.point.converter;
import com.example.authservice.domain.point.dto.PointResDTO;
import com.example.authservice.domain.point.entity.Point;

public class PointConverter {
    public static PointResDTO.pointInformDTO toPointResDTO(Point point) {
        return PointResDTO.pointInformDTO.builder()
                .id(point.getId())
                .isAddPoint(point.getIsAddPoint())
                .updatePoint(point.getUpdatePoint())
                .balance(point.getBalance())
                .description(point.getDescription())
                .createdAt(point.getCreatedAt())
                .build();
    }
}
