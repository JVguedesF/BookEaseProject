package com.bookease.service;

import com.bookease.exception.*;
import com.bookease.model.dto.request.LoginRequestDto;
import com.bookease.model.entity.Role;
import com.bookease.model.entity.User;
import com.bookease.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User activeUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        String privateKeyPath = "app.key";

        try (AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {
            authService = new AuthService(userRepository, passwordEncoder, privateKeyPath);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar mocks", e);
        }

        Role patientRole = new Role();
        patientRole.setName(Role.Values.PATIENT);
        activeUser = User.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .password("hashedpassword")
                .name("Test User")
                .email("test@example.com")
                .phone("123456789")
                .active(true)
                .tokenRevoked(false)
                .roles(Set.of(patientRole))
                .build();
        userId = activeUser.getUserId();
    }

    @Test
    void authenticate_whenCredentialsValid_returnsTokens() {
        LoginRequestDto loginDto = new LoginRequestDto("testuser", "password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("password", "hashedpassword")).thenReturn(true);

        Map<String, String> tokens = authService.authenticate(loginDto);

        assertNotNull(tokens.get("access_token"), "Erro: O access_token não deveria ser nulo.");
        assertNotNull(tokens.get("refresh_token"), "Erro: O refresh_token não deveria ser nulo.");
        assertFalse(tokens.get("access_token").isEmpty(), "Erro: O access_token deveria ser uma string válida.");
        assertFalse(tokens.get("refresh_token").isEmpty(), "Erro: O refresh_token deveria ser uma string válida.");
    }

    @Test
    void authenticate_whenUsernameNull_throwsException() {
        LoginRequestDto loginDto = new LoginRequestDto(null, "password");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> authService.authenticate(loginDto),
                "Erro: Deveria lançar exceção para username nulo.");
        assertEquals("User with identifier null not found", exception.getMessage(), "Erro: Mensagem de exceção incorreta.");
    }

    @Test
    void authenticate_whenPasswordNull_throwsException() {
        LoginRequestDto loginDto = new LoginRequestDto("testuser", null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> authService.authenticate(loginDto),
                "Erro: Deveria lançar exceção para senha nula.");
        assertEquals("Credenciais inválidas", exception.getMessage(), "Erro: Mensagem de exceção incorreta.");
    }

    @Test
    void authenticate_whenUserNotFound_throwsException() {
        LoginRequestDto loginDto = new LoginRequestDto("unknownuser", "password");

        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> authService.authenticate(loginDto),
                "Erro: Deveria lançar exceção para usuário não encontrado.");
        assertEquals("User with identifier unknownuser not found", exception.getMessage(), "Erro: Mensagem de exceção incorreta.");
    }

    @Test
    void authenticate_whenUserInactive_throwsException() {
        LoginRequestDto loginDto = new LoginRequestDto("testuser", "password");
        activeUser.setActive(false);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));

        EntityOperationException exception = assertThrows(EntityOperationException.class, () -> authService.authenticate(loginDto),
                "Erro: Deveria lançar exceção para usuário inativo.");
        assertEquals("Conta inativa", exception.getMessage(), "Erro: Mensagem de exceção incorreta.");
    }

    @Test
    void authenticate_whenPasswordInvalid_throwsException() {
        LoginRequestDto loginDto = new LoginRequestDto("testuser", "wrongpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrongpassword", "hashedpassword")).thenReturn(false);

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> authService.authenticate(loginDto),
                "Erro: Deveria lançar exceção para senha inválida.");
        assertEquals("Credenciais inválidas", exception.getMessage(), "Erro: Mensagem de exceção incorreta.");
    }

    @Test
    void authenticate_whenTokenRevoked_throwsException() {
        LoginRequestDto loginDto = new LoginRequestDto("testuser", "password");
        activeUser.setTokenRevoked(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));

        EntityOperationException exception = assertThrows(EntityOperationException.class, () -> authService.authenticate(loginDto),
                "Erro: Deveria lançar exceção para token revogado.");
        assertEquals("Tokens revogados, faça login novamente", exception.getMessage(), "Erro: Mensagem de exceção incorreta.");
    }

    @Test
    void revokeTokens_whenUserExists_revokesTokens() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));
        when(userRepository.save(activeUser)).thenReturn(activeUser);

        authService.revokeTokens(userId);

        assertTrue(activeUser.isTokenRevoked(), "Erro: O token do usuário deveria estar revogado.");
        verify(userRepository, times(1)).save(activeUser);
    }

    @Test
    void revokeTokens_whenUserNotFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> authService.revokeTokens(userId),
                "Erro: Deveria lançar exceção para usuário não encontrado.");
        assertEquals("User with identifier " + userId + " not found", exception.getMessage(), "Erro: Mensagem de exceção incorreta.");
    }

    @Test
    void revokeTokens_whenTokensAlreadyRevoked_doesNotThrow() {
        activeUser.setTokenRevoked(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));
        when(userRepository.save(activeUser)).thenReturn(activeUser);

        assertDoesNotThrow(() -> authService.revokeTokens(userId),
                "Erro: Não deveria lançar exceção para tokens já revogados.");
        verify(userRepository, times(1)).save(activeUser);
    }

    @Test
    void verifyOwnership_whenUserOwnsEntityWithJwt_doesNotThrow() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("testuser");
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertDoesNotThrow(() -> authService.verifyOwnership(activeUser),
                "Erro: Não deveria lançar exceção para o usuário dono com JWT.");
    }

    @Test
    void verifyOwnership_whenUserOwnsEntityWithUserDetails_doesNotThrow() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertDoesNotThrow(() -> authService.verifyOwnership(activeUser),
                "Erro: Não deveria lançar exceção para o usuário dono com UserDetails.");
    }

    @Test
    void verifyOwnership_whenUserDoesNotOwnEntityWithJwt_throwsException() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("otheruser");
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> authService.verifyOwnership(activeUser),
                "Erro: Deveria lançar exceção para usuário não autorizado com JWT.");
        assertEquals("Você só pode alterar suas próprias informações", exception.getMessage(), "Erro: Mensagem de exceção incorreta.");
    }

    @Test
    void verifyOwnership_whenNoAuthentication_throwsException() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> authService.verifyOwnership(activeUser),
                "Erro: Deveria lançar exceção para falta de autenticação.");
        assertEquals("Você só pode alterar suas próprias informações", exception.getMessage(), "Erro: Mensagem de exceção incorreta.");
    }

    @Test
    void getUserIdByUsername_whenUsernameExists_returnsUserId() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));

        UUID result = authService.getUserIdByUsername("testuser");

        assertEquals(userId, result, "Erro: O ID retornado não corresponde ao esperado.");
    }

    @Test
    void getUserIdByUsername_whenUsernameNotExists_throwsException() {
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> authService.getUserIdByUsername("unknownuser"),
                "Erro: Deveria lançar exceção para username não encontrado.");
        assertEquals("User with identifier unknownuser not found", exception.getMessage(), "Erro: Mensagem de exceção incorreta.");
    }

    @Test
    void getUserIdByUsername_whenUserInactive_throwsException() {
        activeUser.setActive(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> authService.getUserIdByUsername("testuser"),
                "Erro: Deveria lançar exceção para usuário inativo.");
        assertEquals("User with identifier testuser not found", exception.getMessage(), "Erro: Mensagem de exceção incorreta.");
    }

    @Test
    void generateAccessToken_containsCorrectClaims() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("password", "hashedpassword")).thenReturn(true);

        Map<String, String> tokens = authService.authenticate(new LoginRequestDto("testuser", "password"));
        String generatedToken = tokens.get("access_token");

        DecodedJWT decodedJWT = JWT.decode(generatedToken);
        assertEquals("testuser", decodedJWT.getSubject(), "Erro: O subject do access_token está incorreto.");
        assertEquals("BookEase", decodedJWT.getIssuer(), "Erro: O issuer do access_token está incorreto.");
        assertTrue(decodedJWT.getExpiresAt().after(new Date()), "Erro: O access_token deveria ter expiração futura.");
    }

    @Test
    void generateRefreshToken_containsCorrectClaims() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("password", "hashedpassword")).thenReturn(true);

        Map<String, String> tokens = authService.authenticate(new LoginRequestDto("testuser", "password"));
        String generatedToken = tokens.get("refresh_token");

        DecodedJWT decodedJWT = JWT.decode(generatedToken);
        assertEquals("testuser", decodedJWT.getSubject(), "Erro: O subject do refresh_token está incorreto.");
        assertEquals("BookEase", decodedJWT.getIssuer(), "Erro: O issuer do refresh_token está incorreto.");
        assertTrue(decodedJWT.getExpiresAt().after(new Date()), "Erro: O refresh_token deveria ter expiração futura.");
    }
}