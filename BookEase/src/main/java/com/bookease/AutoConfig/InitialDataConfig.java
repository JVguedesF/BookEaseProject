package com.bookease.AutoConfig;

import com.bookease.model.entity.*;
import com.bookease.model.enums.ProcedureEnum;
import com.bookease.model.enums.SpecialityEnum;
import com.bookease.repository.ProcedureRepository;
import com.bookease.repository.RoleRepository;
import com.bookease.repository.SpecialityRepository;
import com.bookease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class InitialDataConfig implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitialDataConfig.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SpecialityRepository specialityRepository;
    private final ProcedureRepository procedureRepository;
    private final PasswordEncoder passwordEncoder;

    private final AdminConfig adminConfig;
    private final DentistConfig dentistConfig;
    private final PatientConfig patientConfig;
    private final ClinicConfig clinicConfig;

    @Override
    @Transactional
    public void run(String... args) {
        initializeSpecialities();
        initializeProcedures();
        initializeAdminRoleAndUser();
        initializeDentistRoleAndUser();
        initializePatientRoleAndUser();
        initializeClinicRoleAndUser();
    }

    private void initializeAdminRoleAndUser() {
        Role adminRole = getOrCreateRole(Role.Values.ADMIN);

        if (!userRepository.existsByUsername(adminConfig.getUsername())) {
            User admin = User.builder()
                    .username(adminConfig.getUsername())
                    .password(passwordEncoder.encode(adminConfig.getPassword()))
                    .name("Administrador")
                    .email(adminConfig.getEmail())
                    .phone(adminConfig.getPhone())
                    .active(true)
                    .tokenRevoked(false)
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
            logger.info("Usuário admin criado com sucesso.");
        } else {
            logger.info("Usuário admin já existe no sistema.");
        }
    }

    private void initializeDentistRoleAndUser() {
        Role dentistRole = getOrCreateRole(Role.Values.DOCTOR);

        if (!userRepository.existsByUsername(dentistConfig.getUsername())) {
            User dentist = User.builder()
                    .username(dentistConfig.getUsername())
                    .password(passwordEncoder.encode(dentistConfig.getPassword()))
                    .name(dentistConfig.getName())
                    .email(dentistConfig.getEmail())
                    .phone(dentistConfig.getPhone())
                    .active(true)
                    .tokenRevoked(false)
                    .roles(Set.of(dentistRole))
                    .build();
            userRepository.save(dentist);
            logger.info("Usuário dentista criado com sucesso.");
        } else {
            logger.info("Usuário dentista já existe no sistema.");
        }
    }

    private void initializePatientRoleAndUser() {
        Role patientRole = getOrCreateRole(Role.Values.PATIENT);

        if (!userRepository.existsByUsername(patientConfig.getUsername())) {
            User patient = User.builder()
                    .username(patientConfig.getUsername())
                    .password(passwordEncoder.encode(patientConfig.getPassword()))
                    .name(patientConfig.getName())
                    .email(patientConfig.getEmail())
                    .phone(patientConfig.getPhone())
                    .active(true)
                    .tokenRevoked(false)
                    .roles(Set.of(patientRole))
                    .build();
            userRepository.save(patient);
            logger.info("Usuário paciente criado com sucesso.");
        } else {
            logger.info("Usuário paciente já existe no sistema.");
        }
    }

    private void initializeClinicRoleAndUser() {
        Role clinicRole = getOrCreateRole(Role.Values.CLINIC);

        if (!userRepository.existsByUsername(clinicConfig.getUsername())) {
            User clinic = User.builder()
                    .username(clinicConfig.getUsername())
                    .password(passwordEncoder.encode(clinicConfig.getPassword()))
                    .name(clinicConfig.getName())
                    .email(clinicConfig.getEmail())
                    .phone(clinicConfig.getPhone())
                    .active(true)
                    .tokenRevoked(false)
                    .roles(Set.of(clinicRole))
                    .build();
            userRepository.save(clinic);
            logger.info("Usuário clínica criado com sucesso.");
        } else {
            logger.info("Usuário clínica já existe no sistema.");
        }
    }

    private void initializeSpecialities() {
        if (specialityRepository.count() == 0) {
            Arrays.stream(SpecialityEnum.values())
                    .map(enumValue -> Speciality.builder()
                            .name(enumValue.getDisplayName())
                            .active(true)
                            .build())
                    .forEach(specialityRepository::save);
            logger.info("Especialidades inicializadas com sucesso.");
        } else {
            logger.info("Especialidades já existem no sistema.");
        }
    }

    private void initializeProcedures() {
        if (procedureRepository.count() == 0) {
            Arrays.stream(ProcedureEnum.values())
                    .map(enumValue -> Procedure.builder()
                            .procedureEnum(enumValue)
                            .active(true)
                            .build())
                    .forEach(procedureRepository::save);
            logger.info("Procedimentos inicializados com sucesso.");
        } else {
            logger.info("Procedimentos já existem no sistema.");
        }
    }

    private Role getOrCreateRole(Role.Values roleValue) {
        return roleRepository.findByName(roleValue)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleValue);
                    return roleRepository.save(role);
                });
    }
}