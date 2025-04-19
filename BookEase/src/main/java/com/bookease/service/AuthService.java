package com.bookease.service;

import com.bookease.exception.*;
import com.bookease.model.dto.request.LoginRequestDto;
import com.bookease.model.entity.User;
import com.bookease.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String privateKeyPath;

    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${jwt.private.key}") String privateKeyPath) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.privateKeyPath = privateKeyPath;
    }

    public Map<String, String> authenticate(LoginRequestDto loginDto) {
        User user = userRepository.findByUsername(loginDto.username())
                .orElseThrow(() -> new EntityNotFoundException("User", loginDto.username()));

        if (!user.isActive()) {
            throw new EntityOperationException("Conta inativa");
        }
        if (user.isTokenRevoked()) {
            throw new EntityOperationException("Tokens revogados, faça login novamente");
        }
        if (!passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            throw new AuthenticationException("Credenciais inválidas");
        }

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", generateAccessToken(user));
        tokens.put("refresh_token", generateRefreshToken(user));

        return tokens;
    }

    @Transactional
    public void revokeTokens(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        user.setTokenRevoked(true);
        userRepository.save(user);
    }

    public void verifyOwnership(User entityUser) {
        String authenticatedUsername = getAuthenticatedUsername();
        if (!entityUser.getUsername().equals(authenticatedUsername)) {
            throw new AuthorizationException("Você só pode alterar suas próprias informações");
        }
    }

    public UUID getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .filter(User::isActive)
                .map(User::getUserId)
                .orElseThrow(() -> new EntityNotFoundException("User", username));
    }

    private String generateAccessToken(User user) {
        try {
            RSAPrivateKey privateKey = loadPrivateKey();
            Algorithm algorithm = Algorithm.RSA256(null, privateKey);

            Instant now = Instant.now();
            Instant exp = now.plus(15, ChronoUnit.MINUTES);

            return JWT.create()
                    .withSubject(user.getUsername())
                    .withClaim("roles", user.getRoles().stream()
                            .map(role -> "ROLE_" + role.getName().name())
                            .toList())
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(exp))
                    .withIssuer("BookEase")
                    .sign(algorithm);
        } catch (Exception e) {
            throw new TokenGenerationException("Erro ao gerar access token", e);
        }
    }

    private String generateRefreshToken(User user) {
        try {
            RSAPrivateKey privateKey = loadPrivateKey();
            Algorithm algorithm = Algorithm.RSA256(null, privateKey);

            Instant now = Instant.now();
            Instant exp = now.plus(30, ChronoUnit.DAYS);

            return JWT.create()
                    .withSubject(user.getUsername())
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(exp))
                    .withIssuer("BookEase")
                    .sign(algorithm);
        } catch (Exception e) {
            throw new TokenGenerationException("Erro ao gerar refresh token", e);
        }
    }

    private RSAPrivateKey loadPrivateKey() {
        try {
            String privateKeyContent = new String(Files.readAllBytes(Paths.get(new ClassPathResource(privateKeyPath).getURI())));
            privateKeyContent = privateKeyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new PublicKeyLoadingException("Erro ao carregar chave privada do caminho: " + privateKeyPath, e);
        } catch (Exception e) {
            throw new PublicKeyLoadingException("Erro inesperado ao carregar chave privada", e);
        }
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return switch (principal) {
            case Jwt jwt -> {
                String subject = jwt.getSubject();
                logger.debug("JWT Subject: {}", subject);
                yield subject;
            }
            case UserDetails userDetails -> {
                String username = userDetails.getUsername();
                logger.debug("UserDetails Username: {}", username);
                yield username;
            }
            default -> {
                String result = principal.toString();
                logger.debug("Fallback toString: {}", result);
                yield result;
            }
        };
    }
}