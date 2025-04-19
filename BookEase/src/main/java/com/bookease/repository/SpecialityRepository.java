package com.bookease.repository;

import com.bookease.model.entity.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecialityRepository extends JpaRepository<Speciality, UUID> {

    Optional<Speciality> findByName(String name);

    @Query("SELECT s FROM Speciality s WHERE s.name IN :names")
    List<Speciality> findByNameIn(List<String> names);
    long count();

    @Query("SELECT s FROM Speciality s WHERE UPPER(s.name) IN :names")
    List<Speciality> findByNameInIgnoreCase(@Param("names") List<String> names);

    @Query("SELECT s FROM Speciality s WHERE s.name IN :names")
    List<Speciality> findAllByNameIn(@Param("names") List<String> names);

    boolean existsByNameIgnoreCase(String name);
}
