package com.islamiclearningcenter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.islamiclearningcenter.auth.AccessAndRefreshTokens;
import com.islamiclearningcenter.auth.JwtAccessTokenService;
import com.islamiclearningcenter.auth.RefreshTokenService;
import com.islamiclearningcenter.domain.User;
import com.islamiclearningcenter.domain.UserRole;
import com.islamiclearningcenter.exception.InvalidCredentialsException;
import com.islamiclearningcenter.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtAccessTokenService jwtAccessTokenService;

  @Mock private RefreshTokenService refreshTokenService;

  @InjectMocks private LoginService loginService;

  @Test
  @DisplayName("login throws InvalidCredentialsException when email is unknown")
  void login_unknownEmail_throws() {
    when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> loginService.login("A@B.com", "password123"))
        .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  @DisplayName("login throws InvalidCredentialsException when password does not match")
  void login_wrongPassword_throws() {
    User user = new User();
    user.setId(9L);
    user.setEmail("a@b.com");
    user.setPasswordHash("HASH");
    user.setRole(UserRole.STUDENT);
    when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong", "HASH")).thenReturn(false);

    assertThatThrownBy(() -> loginService.login("a@b.com", "wrong"))
        .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  @DisplayName("login returns access and refresh tokens when credentials are valid")
  void login_success_returnsTokens() {
    User user = new User();
    user.setId(3L);
    user.setEmail("teacher@example.com");
    user.setPasswordHash("STORED_HASH");
    user.setRole(UserRole.TEACHER);
    when(userRepository.findByEmail("teacher@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("correct", "STORED_HASH")).thenReturn(true);
    when(jwtAccessTokenService.createAccessToken(3L, "teacher@example.com", "TEACHER"))
        .thenReturn("jwt-here");
    when(refreshTokenService.issueRefreshToken(user)).thenReturn("refresh-here");

    AccessAndRefreshTokens tokens = loginService.login("teacher@example.com", "correct");

    assertThat(tokens.accessToken()).isEqualTo("jwt-here");
    assertThat(tokens.refreshToken()).isEqualTo("refresh-here");
    verify(jwtAccessTokenService).createAccessToken(eq(3L), eq("teacher@example.com"), eq("TEACHER"));
    verify(refreshTokenService).issueRefreshToken(any(User.class));
  }

  @Test
  @DisplayName("login rejects blank email or password")
  void login_blankInputs_throws() {
    assertThatThrownBy(() -> loginService.login("", "x"))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> loginService.login("a@b.com", ""))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
