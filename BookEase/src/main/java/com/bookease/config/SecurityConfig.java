package com.bookease.config;

import com.bookease.exception.PublicKeyLoadingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_DOCTOR = "DOCTOR";
    private static final String ROLE_PATIENT = "PATIENT";
    private static final String ROLE_CLINIC = "CLINIC";

    private static final String[] PUBLIC_ENDPOINTS = {"/auth/login", "/patient/register"};
    private static final String[] PATIENT_INFO_ENDPOINTS = {"/patient/{patientId}", "/patient/cpf/{cpf}", "/patient/name"};
    private static final String[] DOCTOR_INFO_ENDPOINTS = {"/doctor/{doctorId}", "/doctor/name", "/doctor/speciality"};
    private static final String[] CLINIC_INFO_ENDPOINTS = {"/clinic/{clinicId}", "/clinic/cnpj/{cnpj}", "/clinic/name", "/clinic/city"};
    private static final String[] DOCTOR_CLINIC_ENDPOINTS = {"/doctor-clinic/{doctorClinicId}", "/doctor-clinic/doctor/{doctorId}", "/doctor-clinic/clinic/{clinicId}", "/doctor-clinic/doctors/{clinicId}", "/doctor-clinic/clinics/{doctorId}"};

    @Value("${jwt.public.key}")
    private String publicKeyPath;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(CsrfConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(PATIENT_INFO_ENDPOINTS).hasAnyRole(ROLE_CLINIC, ROLE_DOCTOR, ROLE_ADMIN, ROLE_PATIENT)
                        .requestMatchers("/patient/meu-perfil").hasRole(ROLE_PATIENT)
                        .requestMatchers("/admin/**").hasRole(ROLE_ADMIN)
                        .requestMatchers("/doctor/register").hasRole(ROLE_ADMIN)
                        .requestMatchers(DOCTOR_INFO_ENDPOINTS).hasAnyRole(ROLE_PATIENT, ROLE_DOCTOR, ROLE_CLINIC, ROLE_ADMIN)
                        .requestMatchers(CLINIC_INFO_ENDPOINTS).hasAnyRole(ROLE_PATIENT, ROLE_DOCTOR, ROLE_CLINIC, ROLE_ADMIN)
                        .requestMatchers(DOCTOR_CLINIC_ENDPOINTS).hasAnyRole(ROLE_CLINIC, ROLE_ADMIN, ROLE_DOCTOR, ROLE_PATIENT)
                        .requestMatchers(HttpMethod.POST, "/api/procedures-offered").hasAnyRole(ROLE_DOCTOR, ROLE_CLINIC, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/procedures-offered/**").hasAnyRole(ROLE_PATIENT, ROLE_DOCTOR, ROLE_CLINIC, ROLE_ADMIN)
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/auth/login", "/patient/register");
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            ClassPathResource resource = new ClassPathResource(publicKeyPath);

            if (!resource.exists()) {
                throw new PublicKeyLoadingException("Arquivo de chave pública não encontrado no caminho: " + publicKeyPath);
            }

            String publicKeyContent = new String(Files.readAllBytes(Paths.get(resource.getURI())));

            publicKeyContent = publicKeyContent
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = Base64.getDecoder().decode(publicKeyContent);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

            return NimbusJwtDecoder.withPublicKey(publicKey).build();
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new PublicKeyLoadingException("Erro ao carregar a chave pública: " + e.getMessage(), e);
        }
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles == null) {
                return java.util.Collections.emptyList();
            }
            return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(java.util.stream.Collectors.toList());
        });
        return converter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
