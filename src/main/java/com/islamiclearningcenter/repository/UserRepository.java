package com.islamiclearningcenter.repository;

import com.islamiclearningcenter.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);
}
