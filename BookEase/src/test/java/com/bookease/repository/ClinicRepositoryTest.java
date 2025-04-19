package com.bookease.repository;

import com.bookease.model.entity.Clinic;
import com.bookease.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicRepositoryTest {

    @Mock
    private ClinicRepository clinicRepository;

    @Test
    void findById_shouldReturnClinic_whenClinicExistsAndIsActive() {
        UUID clinicId = UUID.randomUUID();
        Clinic clinic = Clinic.builder().id(clinicId).active(true).build();
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.of(clinic));
        Optional<Clinic> result = clinicRepository.findById(clinicId);
        assertTrue(result.isPresent());
        assertEquals(clinicId, result.get().getId());
        assertTrue(result.get().isActive());
    }

    @Test
    void findById_shouldReturnEmpty_whenClinicNotFound() {
        UUID clinicId = UUID.randomUUID();
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.empty());
        Optional<Clinic> result = clinicRepository.findById(clinicId);
        assertFalse(result.isPresent());
    }

    @Test
    void findByCnpj_shouldReturnClinic_whenClinicExistsAndIsActive() {
        String cnpj = "12345678901234";
        Clinic clinic = Clinic.builder().cnpj(cnpj).active(true).build();
        when(clinicRepository.findByCnpj(cnpj)).thenReturn(Optional.of(clinic));
        Optional<Clinic> result = clinicRepository.findByCnpj(cnpj);
        assertTrue(result.isPresent());
        assertEquals(cnpj, result.get().getCnpj());
        assertTrue(result.get().isActive());
    }

    @Test
    void findByCnpj_shouldReturnEmpty_whenClinicNotFound() {
        String cnpj = "12345678901234";
        when(clinicRepository.findByCnpj(cnpj)).thenReturn(Optional.empty());
        Optional<Clinic> result = clinicRepository.findByCnpj(cnpj);
        assertFalse(result.isPresent());
    }

    @Test
    void findAllByCity_shouldReturnClinic_whenClinicExistsAndIsActive() {
        String city = "Sao Paulo";
        Clinic clinic = Clinic.builder().city(city).active(true).build();
        when(clinicRepository.findAllByCity(city)).thenReturn(List.of(clinic));
        List<Clinic> result = clinicRepository.findAllByCity(city);


        assertFalse(result.isEmpty());
        assertEquals(city, result.getFirst().getCity());
        assertTrue(result.getFirst().isActive());
    }

    @Test
    void findAllByCity_shouldReturnEmpty_whenClinicNotFound() {
        String city = "Sao Paulo";
        when(clinicRepository.findAllByCity(city)).thenReturn(List.of());
        List<Clinic> result = clinicRepository.findAllByCity(city);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserName_shouldReturnClinic_whenClinicExistsAndIsActive() {
        String name = "ClinicTest";
        User user = User.builder().name(name).build();
        Clinic clinic = Clinic.builder().user(user).active(true).build();
        when(clinicRepository.findByUserName(name)).thenReturn(List.of(clinic));
        List<Clinic> result = clinicRepository.findByUserName(name);

        assertFalse(result.isEmpty());
        assertEquals(name, result.getFirst().getUser().getName());
        assertTrue(result.getFirst().isActive());
    }

    @Test
    void findByUserName_shouldReturnEmpty_whenClinicNotFound() {
        String name = "ClinicTest";
        when(clinicRepository.findByUserName(name)).thenReturn(List.of());
        List<Clinic> result = clinicRepository.findByUserName(name);

        assertTrue(result.isEmpty());
    }
}