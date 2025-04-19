package com.bookease.model.entity;

import com.bookease.model.enums.AppointmentEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "notes")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentEnum status;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ProcedureOffered procedureOffered;

    @ManyToOne
    @JoinColumn(nullable = false)
    private DoctorClinic doctorClinic;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "work_schedule_id", nullable = false)
    private WorkSchedule workSchedule;
}
