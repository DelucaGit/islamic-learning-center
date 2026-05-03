package com.islamiclearningcenter.service;

import com.islamiclearningcenter.auth.AccessAndRefreshTokens;
import com.islamiclearningcenter.auth.JwtAccessTokenService;
import com.islamiclearningcenter.auth.RefreshTokenService;
import com.islamiclearningcenter.domain.User;
import com.islamiclearningcenter.exception.InvalidCredentialsException;
import com.islamiclearningcenter.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtAccessTokenService jwtAccessTokenService;
  private final RefreshTokenService refreshTokenService;

  public LoginService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtAccessTokenService jwtAccessTokenService,
      RefreshTokenService refreshTokenService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtAccessTokenService = jwtAccessTokenService;
    this.refreshTokenService = refreshTokenService;
  }

  /**
   * Validates credentials and returns new access and refresh tokens.
   *
   * @throws IllegalArgumentException if email or password is blank
   * @throws InvalidCredentialsException if there is no user or the password does not match
   */
  @Transactional
  public AccessAndRefreshTokens login(String email, String rawPassword) {
    if (email == null || email.isBlank() || rawPassword == null || rawPassword.isBlank()) {
      throw new IllegalArgumentException("email and password are required");
    }
    String normalizedEmail = RegistrationService.normalizeEmail(email);
    User user =
        userRepository
            .findByEmail(normalizedEmail)
            .orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
      throw new InvalidCredentialsException();
    }

    String accessToken =
        jwtAccessTokenService.createAccessToken(
            user.getId(), user.getEmail(), user.getRole().name());
    String refreshToken = refreshTokenService.issueRefreshToken(user);
    return new AccessAndRefreshTokens(accessToken, refreshToken);
  }
}
