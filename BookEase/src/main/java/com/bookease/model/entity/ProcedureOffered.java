package com.bookease.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_procedure_offered")
public class ProcedureOffered {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_clinic_id")
    private DoctorClinic doctorClinic;

    @ManyToOne(optional = false)
    @JoinColumn(name = "procedure_id")
    private Procedure procedure;

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private boolean active = true;
}
