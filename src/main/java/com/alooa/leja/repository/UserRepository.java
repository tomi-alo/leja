package com.alooa.leja.repository;

import com.alooa.leja.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
