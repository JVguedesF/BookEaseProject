package com.bookease.repository;

import com.bookease.model.entity.Doctor;
import com.bookease.model.entity.Role;
import com.bookease.model.entity.Speciality;
import com.bookease.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User createUser(String name, Set<Role> roles) {
        User user = User.builder()
                .username(name + "@example.com")
                .password("password")
                .name(name)
                .roles(roles)
                .active(true)
                .build();
        return userRepository.save(user);
    }

    private Role createRole(String name) {
        Role.Values roleValue = Role.Values.valueOf(name.toUpperCase());
        return roleRepository.findByName(roleValue)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleValue).build()));
    }

    @SuppressWarnings("SameParameterValue")
    private Speciality createSpeciality(String name) {
        Speciality speciality = Speciality.builder()
                .name(name)
                .build();
        return specialityRepository.save(speciality);
    }

    private Doctor createDoctor(String crm, User user, Set<Speciality> specialities, boolean active) {
        Doctor doctor = Doctor.builder()
                .crm(crm)
                .user(user)
                .specialities(specialities)
                .active(active)
                .build();
        return doctorRepository.save(doctor);
    }

    @Test
    void testFindByIdWhenDoctorExistsAndIsActive() {
        User user = createUser("Dr. John", Set.of(createRole("DOCTOR")));
        Doctor doctor = createDoctor("12345", user, Set.of(), true);
        Optional<Doctor> foundDoctor = doctorRepository.findById(doctor.getId());
        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get().getId()).isEqualTo(doctor.getId());
    }

    @Test
    void testFindByIdWhenDoctorDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Doctor> foundDoctor = doctorRepository.findById(nonExistentId);
        assertThat(foundDoctor).isNotPresent();
    }

    @Test
    void testFindByIdWhenDoctorIsInactive() {
        User user = createUser("Dr. John", Set.of(createRole("DOCTOR")));
        Doctor doctor = createDoctor("12345", user, Set.of(), false);
        Optional<Doctor> foundDoctor = doctorRepository.findById(doctor.getId());
        assertThat(foundDoctor).isNotPresent();
    }

    @Test
    void testFindByCrmWhenDoctorExistsAndIsActive() {
        User user = createUser("Dr. Jane", Set.of(createRole("DOCTOR")));
        Doctor doctor = createDoctor("67890", user, Set.of(), true);
        Optional<Doctor> foundDoctor = doctorRepository.findByCrm(doctor.getCrm());
        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get().getCrm()).isEqualTo(doctor.getCrm());
    }

    @Test
    void testFindByCrmWhenDoctorIsInactive() {
        User user = createUser("Dr. Jane", Set.of(createRole("DOCTOR")));
        Doctor doctor = createDoctor("67890", user, Set.of(), false);
        Optional<Doctor> foundDoctor = doctorRepository.findByCrm(doctor.getCrm());
        assertThat(foundDoctor).isNotPresent();
    }

    @Test
    void testFindAllBySpecialityWhenDoctorsExist() {
        Speciality ortho = createSpeciality("Ortodontia");
        Role doctorRole = createRole("DOCTOR");
        User user1 = createUser("Dr. Alice", Set.of(doctorRole));
        User user2 = createUser("Dr. Bob", Set.of(doctorRole));
        createDoctor("11111", user1, Set.of(ortho), true);
        createDoctor("22222", user2, Set.of(ortho), true);
        List<Doctor> doctors = doctorRepository.findAllBySpeciality("Ortodontia");
        assertThat(doctors).hasSize(2);
    }

    @Test
    void testFindAllBySpecialityWhenNoDoctorsExist() {
        List<Doctor> doctors = doctorRepository.findAllBySpeciality("Endodontia");
        assertThat(doctors).isEmpty();
    }

    @Test
    void testFindByUserNameWithDoctorRoleWhenDoctorExists() {
        Role doctorRole = createRole("DOCTOR");
        User user = createUser("Dr. Mark", Set.of(doctorRole));
        createDoctor("33333", user, Set.of(), true);
        Optional<Doctor> foundDoctor = doctorRepository.findByUserNameWithDoctorRole("Dr. Mark");
        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get().getUser().getName()).isEqualTo("Dr. Mark");
    }

    @Test
    void testFindByUserNameWithDoctorRoleWhenRoleIsNotDoctor() {
        Role patientRole = createRole("PATIENT");
        User user = createUser("Dr. Mark", Set.of(patientRole));
        createDoctor("33333", user, Set.of(), true);
        Optional<Doctor> foundDoctor = doctorRepository.findByUserNameWithDoctorRole("Dr. Mark");
        assertThat(foundDoctor).isNotPresent();
    }
}