package com.bookease.repository;

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
class UserRepositoryTest {

    private static final Logger logger = Logger.getLogger(UserRepositoryTest.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        logger.info("Preparando dados para o teste...");
        Role patientRole = new Role();
        patientRole.setName(Role.Values.PATIENT);
        entityManager.persist(patientRole);

        user = User.builder()
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
        userId = user.getUserId();
        entityManager.flush();
        logger.info("Dados preparados: User ID = " + userId);
    }

    @Test
    void findById_whenUserExistsAndActive_returnsUser() {
        logger.info("Testando findById com usuário existente e ativo...");
        Optional<User> foundUser = userRepository.findById(userId);
        assertTrue(foundUser.isPresent(), "Erro: O usuário com ID " + userId + " deveria ser encontrado.");
        assertEquals(userId, foundUser.get().getUserId(), "Erro: O ID do usuário retornado não corresponde ao esperado.");
        assertTrue(foundUser.get().isActive(), "Erro: O usuário retornado deveria estar ativo.");
        logger.info("Sucesso: Usuário encontrado com ID " + userId + " e ativo.");
    }

    @Test
    void findById_whenUserNotExists_returnsEmpty() {
        UUID randomId = UUID.randomUUID();
        logger.info("Testando findById com ID inexistente: " + randomId);
        Optional<User> foundUser = userRepository.findById(randomId);
        assertFalse(foundUser.isPresent(), "Erro: Não deveria encontrar um usuário com o ID inexistente " + randomId);
        logger.info("Sucesso: Nenhum usuário encontrado para o ID " + randomId);
    }

    @Test
    void findById_whenUserInactive_returnsEmpty() {
        logger.info("Testando findById com usuário inativo...");
        user.setActive(false);
        entityManager.persist(user);
        entityManager.flush();
        Optional<User> foundUser = userRepository.findById(userId);
        assertFalse(foundUser.isPresent(), "Erro: Não deveria encontrar o usuário inativo com ID " + userId);
        logger.info("Sucesso: Usuário inativo com ID " + userId + " não foi encontrado.");
    }

    @Test
    void findByUsername_whenUsernameExistsAndActive_returnsUser() {
        logger.info("Testando findByUsername com username existente e ativo...");
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertTrue(foundUser.isPresent(), "Erro: O usuário com username 'testuser' deveria ser encontrado.");
        assertEquals("testuser", foundUser.get().getUsername(), "Erro: O username retornado não corresponde ao esperado.");
        assertTrue(foundUser.get().isActive(), "Erro: O usuário retornado deveria estar ativo.");
        logger.info("Sucesso: Usuário encontrado com username 'testuser' e ativo.");
    }

    @Test
    void findByUsername_whenUsernameNotExists_returnsEmpty() {
        logger.info("Testando findByUsername com username inexistente...");
        Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");
        assertFalse(foundUser.isPresent(), "Erro: Não deveria encontrar um usuário com o username inexistente 'nonexistentuser'.");
        logger.info("Sucesso: Nenhum usuário encontrado para o username 'nonexistentuser'.");
    }

    @Test
    void findByUsername_whenUserInactive_returnsEmpty() {
        logger.info("Testando findByUsername com usuário inativo...");
        user.setActive(false);
        entityManager.persist(user);
        entityManager.flush();
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertFalse(foundUser.isPresent(), "Erro: Não deveria encontrar o usuário inativo com username 'testuser'.");
        logger.info("Sucesso: Usuário inativo com username 'testuser' não foi encontrado.");
    }

    @Test
    void existsByUsername_whenUsernameExistsAndActive_returnsTrue() {
        logger.info("Testando existsByUsername com username existente e ativo...");
        boolean exists = userRepository.existsByUsername("testuser");
        assertTrue(exists, "Erro: Deveria confirmar que o username 'testuser' existe e está ativo.");
        logger.info("Sucesso: Confirmado que o username 'testuser' existe e está ativo.");
    }

    @Test
    void existsByUsername_whenUsernameNotExists_returnsFalse() {
        logger.info("Testando existsByUsername com username inexistente...");
        boolean exists = userRepository.existsByUsername("nonexistentuser");
        assertFalse(exists, "Erro: Não deveria confirmar a existência do username inexistente 'nonexistentuser'.");
        logger.info("Sucesso: Confirmado que o username 'nonexistentuser' não existe.");
    }

    @Test
    void existsByUsername_whenUsernameExistsButInactive_returnsFalse() {
        logger.info("Testando existsByUsername com username de usuário inativo...");
        user.setActive(false);
        entityManager.persist(user);
        entityManager.flush();
        boolean exists = userRepository.existsByUsername("testuser");
        assertFalse(exists, "Erro: Não deveria confirmar a existência do username 'testuser' para um usuário inativo.");
        logger.info("Sucesso: Confirmado que o username 'testuser' de usuário inativo não é considerado existente.");
    }

    @Test
    void existsByEmail_whenEmailExistsAndActive_returnsTrue() {
        logger.info("Testando existsByEmail com email existente e ativo...");
        boolean exists = userRepository.existsByEmail("test@example.com");
        assertTrue(exists, "Erro: Deveria confirmar que o email 'test@example.com' existe e está ativo.");
        logger.info("Sucesso: Confirmado que o email 'test@example.com' existe e está ativo.");
    }

    @Test
    void existsByEmail_whenEmailNotExists_returnsFalse() {
        logger.info("Testando existsByEmail com email inexistente...");
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        assertFalse(exists, "Erro: Não deveria confirmar a existência do email inexistente 'nonexistent@example.com'.");
        logger.info("Sucesso: Confirmado que o email 'nonexistent@example.com' não existe.");
    }

    @Test
    void existsByEmail_whenEmailExistsButInactive_returnsFalse() {
        logger.info("Testando existsByEmail com email de usuário inativo...");
        user.setActive(false);
        entityManager.persist(user);
        entityManager.flush();
        boolean exists = userRepository.existsByEmail("test@example.com");
        assertFalse(exists, "Erro: Não deveria confirmar a existência do email 'test@example.com' para um usuário inativo.");
        logger.info("Sucesso: Confirmado que o email 'test@example.com' de usuário inativo não é considerado existente.");
    }

    @Test
    void existsByPhone_whenPhoneExistsAndActive_returnsTrue() {
        logger.info("Testando existsByPhone com telefone existente e ativo...");
        boolean exists = userRepository.existsByPhone("123456789");
        assertTrue(exists, "Erro: Deveria confirmar que o telefone '123456789' existe e está ativo.");
        logger.info("Sucesso: Confirmado que o telefone '123456789' existe e está ativo.");
    }

    @Test
    void existsByPhone_whenPhoneNotExists_returnsFalse() {
        logger.info("Testando existsByPhone com telefone inexistente...");
        boolean exists = userRepository.existsByPhone("987654321");
        assertFalse(exists, "Erro: Não deveria confirmar a existência do telefone inexistente '987654321'.");
        logger.info("Sucesso: Confirmado que o telefone '987654321' não existe.");
    }

    @Test
    void existsByPhone_whenPhoneExistsButInactive_returnsFalse() {
        logger.info("Testando existsByPhone com telefone de usuário inativo...");
        user.setActive(false);
        entityManager.persist(user);
        entityManager.flush();
        boolean exists = userRepository.existsByPhone("123456789");
        assertFalse(exists, "Erro: Não deveria confirmar a existência do telefone '123456789' para um usuário inativo.");
        logger.info("Sucesso: Confirmado que o telefone '123456789' de usuário inativo não é considerado existente.");
    }
}