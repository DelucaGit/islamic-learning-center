package com.islamiclearningcenter.service;

import com.islamiclearningcenter.domain.User;
import com.islamiclearningcenter.domain.UserRole;
import com.islamiclearningcenter.exception.EmailAlreadyInUseException;
import com.islamiclearningcenter.repository.UserRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

  private static final int MIN_PASSWORD_LENGTH = 8;

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Registers a new user with a bcrypt-hashed password. Email is normalized (trim + lower case).
   *
   * @throws EmailAlreadyInUseException if the email is already taken
   * @throws IllegalArgumentException if the password is too short or inputs are blank
   */
  @Transactional
  public User registerNewUser(String email, String rawPassword, String fullName, UserRole role) {
    requireText(email, "email");
    requireText(rawPassword, "password");
    requireText(fullName, "fullName");
    if (role == null) {
      throw new IllegalArgumentException("role must not be null");
    }
    if (rawPassword.length() < MIN_PASSWORD_LENGTH) {
      throw new IllegalArgumentException(
          "password must be at least " + MIN_PASSWORD_LENGTH + " characters");
    }

    String normalizedEmail = normalizeEmail(email);
    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new EmailAlreadyInUseException(normalizedEmail);
    }

    User user = new User();
    user.setEmail(normalizedEmail);
    user.setPasswordHash(passwordEncoder.encode(rawPassword));
    user.setFullName(fullName.trim());
    user.setRole(role);
    return userRepository.save(user);
  }

  static String normalizeEmail(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private static void requireText(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
  }
}
