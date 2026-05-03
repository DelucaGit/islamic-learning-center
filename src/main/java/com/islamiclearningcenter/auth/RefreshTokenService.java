package com.islamiclearningcenter.auth;

import com.islamiclearningcenter.config.JwtProperties;
import com.islamiclearningcenter.domain.RefreshToken;
import com.islamiclearningcenter.domain.User;
import com.islamiclearningcenter.exception.InvalidCredentialsException;
import com.islamiclearningcenter.repository.RefreshTokenRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final Clock clock;
  private final JwtProperties jwtProperties;
  private final Supplier<String> opaqueTokenGenerator;

  public RefreshTokenService(
      RefreshTokenRepository refreshTokenRepository,
      Clock clock,
      JwtProperties jwtProperties,
      @Qualifier("refreshOpaqueTokenGenerator") Supplier<String> opaqueTokenGenerator) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.clock = clock;
    this.jwtProperties = jwtProperties;
    this.opaqueTokenGenerator = opaqueTokenGenerator;
  }

  /**
   * Persists a fingerprint of a new opaque refresh token and returns the secret value once (send
   * to the client only over HTTPS).
   */
  public String issueRefreshToken(User user) {
    String plain = opaqueTokenGenerator.get();
    String fingerprint = Sha256Hasher.hexDigestUtf8(plain);
    Instant expiresAt = clock.instant().plus(jwtProperties.refreshTokenTtl());

    RefreshToken row = RefreshToken.forUser(user, fingerprint, expiresAt);
    refreshTokenRepository.save(row);
    return plain;
  }

  /** Consumes a refresh token exactly once and returns its user if valid; otherwise throws. */
  public User consumeRefreshToken(String presentedToken) {
    String fingerprint = fingerprintOf(presentedToken);
    RefreshToken row =
        refreshTokenRepository
            .findByTokenHashAndRevokedFalse(fingerprint)
            .orElseThrow(InvalidCredentialsException::new);

    if (!row.isUsableAt(clock.instant())) {
      row.revoke();
      refreshTokenRepository.save(row);
      throw new InvalidCredentialsException();
    }

    row.revoke();
    refreshTokenRepository.save(row);
    return row.getUser();
  }

  public void revokeIfPresent(String presentedToken) {
    if (presentedToken == null || presentedToken.isBlank()) {
      return;
    }
    String fingerprint = Sha256Hasher.hexDigestUtf8(presentedToken.trim());
    refreshTokenRepository.findByTokenHashAndRevokedFalse(fingerprint).ifPresent(row -> {
      row.revoke();
      refreshTokenRepository.save(row);
    });
  }

  private String fingerprintOf(String presentedToken) {
    if (presentedToken == null || presentedToken.isBlank()) {
      throw new InvalidCredentialsException();
    }
    return Sha256Hasher.hexDigestUtf8(presentedToken.trim());
  }
}
