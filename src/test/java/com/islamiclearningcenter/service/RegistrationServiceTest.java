package com.islamiclearningcenter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.islamiclearningcenter.domain.User;
import com.islamiclearningcenter.domain.UserRole;
import com.islamiclearningcenter.exception.EmailAlreadyInUseException;
import com.islamiclearningcenter.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private RegistrationService registrationService;

  @Test
  @DisplayName("registerNewUser throws when email is already taken")
  void registerNewUser_duplicateEmail_throws() {
    when(userRepository.existsByEmail("a@b.com")).thenReturn(true);

    assertThatThrownBy(
            () ->
                registrationService.registerNewUser(
                    "A@B.com", "password123", "Full Name", UserRole.STUDENT))
        .isInstanceOf(EmailAlreadyInUseException.class);
  }

  @Test
  @DisplayName("registerNewUser saves user with normalized email and encoded password")
  void registerNewUser_success_normalizesAndHashes() {
    when(userRepository.existsByEmail("a@b.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("ENC_HASH");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User saved =
        registrationService.registerNewUser(
            "  A@B.com  ", "password123", "  Learner  ", UserRole.STUDENT);

    assertThat(saved.getEmail()).isEqualTo("a@b.com");
    assertThat(saved.getPasswordHash()).isEqualTo("ENC_HASH");
    assertThat(saved.getFullName()).isEqualTo("Learner");
    assertThat(saved.getRole()).isEqualTo(UserRole.STUDENT);

    verify(passwordEncoder).encode(eq("password123"));
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertThat(captor.getValue().getEmail()).isEqualTo("a@b.com");
  }

  @Test
  @DisplayName("registerNewUser rejects short password")
  void registerNewUser_shortPassword_throws() {
    assertThatThrownBy(
            () ->
                registrationService.registerNewUser(
                    "x@y.com", "short", "Name", UserRole.TEACHER))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("password");
  }
}
