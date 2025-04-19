package com.bookease.repository;

import com.bookease.model.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClinicRepository extends JpaRepository<Clinic, UUID> {
    @Override
    @Query("SELECT c FROM Clinic c WHERE c.id = :uuid AND c.active = true")
    Optional<Clinic> findById(@Param("uuid") UUID uuid);

    @Query("SELECT c FROM Clinic c WHERE c.cnpj = :cnpj AND c.active = true")
    Optional<Clinic> findByCnpj(@Param("cnpj") String cnpj);

    @Query("SELECT c FROM Clinic c WHERE c.city = :city AND c.active = true")
    List<Clinic> findAllByCity(@Param("city") String city);

    @Query("SELECT c FROM Clinic c JOIN c.user u WHERE u.name = :name AND c.active = true")
    List<Clinic> findByUserName(@Param("name") String name);
}
