package com.islamiclearningcenter.auth;

import com.islamiclearningcenter.config.JwtProperties;
import com.islamiclearningcenter.domain.RefreshToken;
import com.islamiclearningcenter.domain.User;
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
}
