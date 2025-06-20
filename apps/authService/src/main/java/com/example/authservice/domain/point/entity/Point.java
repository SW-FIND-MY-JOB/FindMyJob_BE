package com.example.authservice.domain.point.entity;

import com.example.authservice.domain.common.BaseEntity;
import com.example.authservice.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "is_add_point", nullable = false)
    private Boolean isAddPoint;

    @Column(name = "update_point", nullable = false)
    private Integer updatePoint;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
