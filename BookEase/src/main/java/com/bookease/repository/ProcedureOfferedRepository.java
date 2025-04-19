package com.bookease.repository;

import com.bookease.model.enums.ProcedureEnum;
import com.bookease.model.entity.ProcedureOffered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProcedureOfferedRepository extends JpaRepository<ProcedureOffered, UUID> {

    @Query("SELECT COUNT(p) > 0 FROM ProcedureOffered p WHERE p.id = :id AND p.active = true")
    boolean existsByIdAndActiveTrue(@Param("id") UUID id);

    @Query("SELECT COUNT(p) FROM ProcedureOffered p WHERE p.doctorClinic.id = :doctorClinicId AND p.active = true")
    long countByDoctorClinicIdAndActiveTrue(@Param("doctorClinicId") UUID doctorClinicId);

    @Query("SELECT p FROM ProcedureOffered p WHERE p.price >= :minPrice AND p.price <= :maxPrice")
    List<ProcedureOffered> findByPriceBetween(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);

    @Query("SELECT p FROM ProcedureOffered p WHERE p.doctorClinic.id = :doctorClinicId AND p.active = true ORDER BY p.price ASC")
    List<ProcedureOffered> findActiveByDoctorClinicIdOrderByPriceAsc(@Param("doctorClinicId") UUID doctorClinicId);

    @Query("SELECT p FROM ProcedureOffered p WHERE p.doctorClinic.id = :doctorClinicId AND p.active = true AND p.price >= :minPrice AND p.price <= :maxPrice")
    List<ProcedureOffered> findActiveByDoctorClinicIdAndPriceBetween(
            @Param("doctorClinicId") UUID doctorClinicId,
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice
    );

    @Query("SELECT p FROM ProcedureOffered p WHERE p.procedure.displayName = :procedureName AND p.active = true")
    List<ProcedureOffered> findByProcedureNameAndActiveTrue(@Param("procedureName") String procedureName);

    @Query("SELECT po FROM ProcedureOffered po WHERE po.doctorClinic.id = :doctorClinicId AND po.durationMinutes >= :minDuration AND po.active = true")
    List<ProcedureOffered> findByDoctorClinicIdAndDurationMinutesGreaterThanEqualAndActiveTrue(
            @Param("doctorClinicId") UUID doctorClinicId,
            @Param("minDuration") int minDuration
    );

    @Query("SELECT po FROM ProcedureOffered po WHERE po.procedure.displayName = :displayName AND po.active = true")
    List<ProcedureOffered> findByProcedureDisplayNameAndActiveTrue(@Param("displayName") String displayName);

    @Query("SELECT po FROM ProcedureOffered po WHERE po.procedure.procedureEnum = :procedureEnum AND po.active = true ORDER BY po.price ASC")
    List<ProcedureOffered> findByProcedureEnumAndActiveTrueOrderByPriceAsc(@Param("procedureEnum") ProcedureEnum procedureEnum);

    @Query("SELECT po FROM ProcedureOffered po WHERE po.doctorClinic.id = :doctorClinicId AND po.procedure.displayName LIKE %:keyword% AND po.active = true")
    List<ProcedureOffered> findByDoctorClinicIdAndProcedureDisplayNameContainingAndActiveTrue(
            @Param("doctorClinicId") UUID doctorClinicId,
            @Param("keyword") String keyword
    );

    @Query("SELECT po FROM ProcedureOffered po WHERE po.durationMinutes = :duration AND po.price BETWEEN :minPrice AND :maxPrice AND po.active = true")
    List<ProcedureOffered> findByDurationMinutesAndPriceBetweenAndActiveTrue(
            @Param("duration") int duration,
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice
    );

    @Query("SELECT po FROM ProcedureOffered po WHERE po.doctorClinic.id = :doctorClinicId AND po.active = true ORDER BY po.durationMinutes ASC")
    List<ProcedureOffered> findByDoctorClinicIdAndActiveTrueOrderByDurationMinutesAsc(@Param("doctorClinicId") UUID doctorClinicId);

    @Query("SELECT COUNT(po) FROM ProcedureOffered po WHERE po.doctorClinic.id = :doctorClinicId AND po.procedure.procedureEnum = :procedureEnum AND po.active = true")
    Long countByDoctorClinicIdAndProcedureEnumAndActiveTrue(
            @Param("doctorClinicId") UUID doctorClinicId,
            @Param("procedureEnum") ProcedureEnum procedureEnum
    );
}