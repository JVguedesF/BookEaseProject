package com.bookease.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_role")
public class Role {
    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID roleId;

    @Column(name = "name", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private Values name;

    public enum Values {
        ADMIN,
        CLINIC,
        DOCTOR,
        PATIENT
    }
}