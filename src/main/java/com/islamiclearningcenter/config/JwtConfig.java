package com.islamiclearningcenter.config;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.Base64;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

  @Bean
  public Clock systemUtcClock() {
    return Clock.systemUTC();
  }

  /**
   * Produces high-entropy opaque strings for refresh tokens (only the fingerprint is stored in
   * the database).
   */
  @Bean
  @Qualifier("refreshOpaqueTokenGenerator")
  public Supplier<String> refreshOpaqueTokenGenerator() {
    Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    SecureRandom secureRandom = new SecureRandom();
    return () -> {
      byte[] raw = new byte[32];
      secureRandom.nextBytes(raw);
      return encoder.encodeToString(raw);
    };
  }
}
