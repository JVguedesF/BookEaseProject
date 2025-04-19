package com.bookease.repository;

import com.bookease.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Override
    @Query("SELECT u FROM User u WHERE u.userId = :id AND u.active = true")
    Optional<User> findById(@NonNull @Param("id") UUID id);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.active = true")
    Optional<User> findByUsername(@NonNull @Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.active = true")
    boolean existsByUsername(@NonNull @Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
    Optional<User> findByEmail(@NonNull @Param("email") String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.active = true")
    boolean existsByEmail(@NonNull @Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.phone = :phone AND u.active = true")
    Optional<User> findByPhone(@NonNull @Param("phone") String phone);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phone = :phone AND u.active = true")
    boolean existsByPhone(@NonNull @Param("phone") String phone);

    @Query("SELECT u FROM User u WHERE (u.email = :email OR u.phone = :phone) AND u.active = false")
    Optional<User> findInactiveUser(@Param("email") String email, @Param("phone") String phone);
}