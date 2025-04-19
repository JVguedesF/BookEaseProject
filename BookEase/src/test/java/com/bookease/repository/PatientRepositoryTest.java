package com.bookease.repository;

import com.bookease.model.entity.Patient;
import com.bookease.model.entity.Role;
import com.bookease.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PatientRepositoryTest {

    private static final Logger logger = Logger.getLogger(PatientRepositoryTest.class.getName());

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UUID patientId;
    private Patient patient;

    @BeforeEach
    void setUp() {
        logger.info("Preparando dados para o teste...");
        Role patientRole = new Role();
        patientRole.setName(Role.Values.PATIENT);
        entityManager.persist(patientRole);

        User user = User.builder()
                .username("testuser")
                .password("hashedpassword")
                .name("Test User")
                .email("test@example.com")
                .phone("123456789")
                .active(true)
                .tokenRevoked(false)
                .roles(Set.of(patientRole))
                .build();
        entityManager.persist(user);

        patient = Patient.builder()
                .cpf("12345678901")
                .active(true)
                .user(user)
                .build();
        entityManager.persist(patient);
        patientId = patient.getId();
        entityManager.flush();
        logger.info("Dados preparados: Patient ID = " + patientId);
    }

    @Test
    void findById_whenPatientExistsAndActive_returnsPatient() {
        logger.info("Testando findById com paciente existente e ativo...");
        Optional<Patient> foundPatient = patientRepository.findById(patientId);
        assertTrue(foundPatient.isPresent(), "Erro: O paciente com ID " + patientId + " deveria ser encontrado.");
        assertEquals(patientId, foundPatient.get().getId(), "Erro: O ID do paciente retornado não corresponde ao esperado.");
        assertTrue(foundPatient.get().isActive(), "Erro: O paciente retornado deveria estar ativo.");
        logger.info("Sucesso: Paciente encontrado com ID " + patientId + " e ativo.");
    }

    @Test
    void findById_whenPatientNotExists_returnsEmpty() {
        UUID randomId = UUID.randomUUID();
        logger.info("Testando findById com ID inexistente: " + randomId);
        Optional<Patient> foundPatient = patientRepository.findById(randomId);
        assertFalse(foundPatient.isPresent(), "Erro: Não deveria encontrar um paciente com o ID inexistente " + randomId);
        logger.info("Sucesso: Nenhum paciente encontrado para o ID " + randomId);
    }

    @Test
    void findById_whenPatientInactive_returnsEmpty() {
        logger.info("Testando findById com paciente inativo...");
        patient.setActive(false);
        entityManager.persist(patient);
        entityManager.flush();
        Optional<Patient> foundPatient = patientRepository.findById(patientId);
        assertFalse(foundPatient.isPresent(), "Erro: Não deveria encontrar o paciente inativo com ID " + patientId);
        logger.info("Sucesso: Paciente inativo com ID " + patientId + " não foi encontrado.");
    }

    @Test
    void findByCpf_whenCpfExistsAndActive_returnsPatient() {
        logger.info("Testando findByCpf com CPF existente e ativo...");
        Optional<Patient> foundPatient = patientRepository.findByCpf("12345678901");
        assertTrue(foundPatient.isPresent(), "Erro: O paciente com CPF 12345678901 deveria ser encontrado.");
        assertEquals("12345678901", foundPatient.get().getCpf(), "Erro: O CPF retornado não corresponde ao esperado.");
        assertTrue(foundPatient.get().isActive(), "Erro: O paciente retornado deveria estar ativo.");
        logger.info("Sucesso: Paciente encontrado com CPF 12345678901 e ativo.");
    }

    @Test
    void findByCpf_whenCpfNotExists_returnsEmpty() {
        logger.info("Testando findByCpf com CPF inexistente...");
        Optional<Patient> foundPatient = patientRepository.findByCpf("00000000000");
        assertFalse(foundPatient.isPresent(), "Erro: Não deveria encontrar um paciente com o CPF inexistente 00000000000.");
        logger.info("Sucesso: Nenhum paciente encontrado para o CPF 00000000000.");
    }

    @Test
    void existsByCpf_whenCpfExistsAndActive_returnsTrue() {
        logger.info("Testando existsByCpf com CPF existente e ativo...");
        boolean exists = patientRepository.existsByCpf("12345678901");
        assertTrue(exists, "Erro: Deveria confirmar que o CPF 12345678901 existe e está ativo.");
        logger.info("Sucesso: Confirmado que o CPF 12345678901 existe e está ativo.");
    }

    @Test
    void existsByCpf_whenCpfNotExists_returnsFalse() {
        logger.info("Testando existsByCpf com CPF inexistente...");
        boolean exists = patientRepository.existsByCpf("00000000000");
        assertFalse(exists, "Erro: Não deveria confirmar a existência do CPF inexistente 00000000000.");
        logger.info("Sucesso: Confirmado que o CPF 00000000000 não existe.");
    }

    @Test
    void existsByCpf_whenCpfExistsButInactive_returnsFalse() {
        logger.info("Testando existsByCpf com CPF de paciente inativo...");
        patient.setActive(false);
        entityManager.persist(patient);
        entityManager.flush();
        boolean exists = patientRepository.existsByCpf("12345678901");
        assertFalse(exists, "Erro: Não deveria confirmar a existência do CPF 12345678901 para um paciente inativo.");
        logger.info("Sucesso: Confirmado que o CPF 12345678901 de paciente inativo não é considerado existente.");
    }

    @Test
    void findByUserNameWithPatientRole_whenNameExistsAndRolePatient_returnsPatient() {
        logger.info("Testando findByUserNameWithPatientRole com nome existente...");
        Optional<Patient> foundPatient = patientRepository.findByUserNameWithPatientRole("Test User");
        assertTrue(foundPatient.isPresent(), "Erro: O paciente com nome 'Test User' deveria ser encontrado.");
        assertEquals("Test User", foundPatient.get().getUser().getName(), "Erro: O nome do usuário retornado não corresponde ao esperado.");
        assertTrue(foundPatient.get().isActive(), "Erro: O paciente retornado deveria estar ativo.");
        logger.info("Sucesso: Paciente encontrado com nome 'Test User' e ativo.");
    }

    @Test
    void findByUserNameWithPatientRole_whenNameNotExists_returnsEmpty() {
        logger.info("Testando findByUserNameWithPatientRole com nome inexistente...");
        Optional<Patient> foundPatient = patientRepository.findByUserNameWithPatientRole("Nonexistent User");
        assertFalse(foundPatient.isPresent(), "Erro: Não deveria encontrar um paciente com o nome inexistente 'Nonexistent User'.");
        logger.info("Sucesso: Nenhum paciente encontrado para o nome 'Nonexistent User'.");
    }

    @Test
    void findByUserNameWithPatientRole_whenPatientInactive_returnsEmpty() {
        logger.info("Testando findByUserNameWithPatientRole com paciente inativo...");
        patient.setActive(false);
        entityManager.persist(patient);
        entityManager.flush();
        Optional<Patient> foundPatient = patientRepository.findByUserNameWithPatientRole("Test User");
        assertFalse(foundPatient.isPresent(), "Erro: Não deveria encontrar o paciente inativo com nome 'Test User'.");
        logger.info("Sucesso: Paciente inativo com nome 'Test User' não foi encontrado.");
    }
}