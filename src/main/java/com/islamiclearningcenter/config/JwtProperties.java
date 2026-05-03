package com.islamiclearningcenter.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, Duration accessTokenTtl) {

  public JwtProperties {
    if (secret == null || secret.isBlank()) {
      throw new IllegalArgumentException("app.jwt.secret must be set (e.g. APP_JWT_SECRET env var)");
    }
    byte[] utf8 = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    if (utf8.length < 32) {
      throw new IllegalArgumentException(
          "app.jwt.secret must be at least 32 UTF-8 bytes for HS256 (use a long random string)");
    }
    if (accessTokenTtl == null || accessTokenTtl.isNegative() || accessTokenTtl.isZero()) {
      throw new IllegalArgumentException("app.jwt.access-token-ttl must be a positive duration");
    }
  }
}
