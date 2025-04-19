package com.bookease.repository;

import com.bookease.model.entity.Speciality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SpecialityRepositoryTest {

    @Autowired
    private SpecialityRepository specialityRepository;

    private UUID specialityId;

    @BeforeEach
    void setUp() {
        Speciality speciality = Speciality.builder()
                .name("Ortodontia")
                .build();


        Speciality savedSpeciality = specialityRepository.save(speciality);
        specialityId = savedSpeciality.getId();
    }

    @Test
    void findById_ShouldReturnSpeciality_WhenIdExists() {
        Optional<Speciality> foundSpeciality = specialityRepository.findById(specialityId);
        assertTrue(foundSpeciality.isPresent(), "A especialidade deveria ser encontrada");
        assertEquals(specialityId, foundSpeciality.get().getId(), "O ID deve corresponder");
        assertEquals("Ortodontia", foundSpeciality.get().getName(), "O nome deve corresponder");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Speciality> foundSpeciality = specialityRepository.findById(nonExistentId);
        assertFalse(foundSpeciality.isPresent(), "Não deveria encontrar uma especialidade com ID inexistente");
    }

    @Test
    void findByName_ShouldReturnSpeciality_WhenNameExists() {
        Optional<Speciality> foundSpeciality = specialityRepository.findByName("Ortodontia");
        assertTrue(foundSpeciality.isPresent(), "A especialidade deveria ser encontrada pelo nome");
        assertEquals(specialityId, foundSpeciality.get().getId(), "O ID deve corresponder");
        assertEquals("Ortodontia", foundSpeciality.get().getName(), "O nome deve corresponder");
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenNameDoesNotExist() {
        Optional<Speciality> foundSpeciality = specialityRepository.findByName("Endodontia");
        assertFalse(foundSpeciality.isPresent(), "Não deveria encontrar uma especialidade com nome inexistente");
    }

    @Test
    void findByName_ShouldBeCaseSensitive_WhenSearching() {
        Optional<Speciality> foundSpeciality = specialityRepository.findByName("ortodontia");
        assertFalse(foundSpeciality.isPresent(), "A busca por nome deve ser sensível a maiúsculas/minúsculas");
    }
}