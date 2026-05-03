package com.islamiclearningcenter.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.islamiclearningcenter.config.JwtProperties;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JwtAccessTokenServiceTest {

  /** Exactly 32 ASCII characters so HS256 key length requirement is satisfied. */
  private static final String TEST_SECRET = "01234567890123456789012345678901";

  @Nested
  @DisplayName("JwtProperties validation")
  class JwtPropertiesValidation {

    @Test
    @DisplayName("rejects blank secret")
    void rejectsBlankSecret() {
      assertThatThrownBy(() -> new JwtProperties("  ", Duration.ofMinutes(5), Duration.ofDays(7)))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("secret");
    }

    @Test
    @DisplayName("rejects secret shorter than 32 UTF-8 bytes")
    void rejectsShortSecret() {
      assertThatThrownBy(() -> new JwtProperties("short", Duration.ofMinutes(5), Duration.ofDays(7)))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("32");
    }

    @Test
    @DisplayName("rejects non-positive access token TTL")
    void rejectsNonPositiveAccessTtl() {
      assertThatThrownBy(() -> new JwtProperties(TEST_SECRET, Duration.ZERO, Duration.ofDays(7)))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("access-token-ttl");
    }

    @Test
    @DisplayName("rejects non-positive refresh token TTL")
    void rejectsNonPositiveRefreshTtl() {
      assertThatThrownBy(() -> new JwtProperties(TEST_SECRET, Duration.ofMinutes(5), Duration.ZERO))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("refresh-token-ttl");
    }
  }

  @Test
  @DisplayName("createAccessToken then parseValid returns the same claims")
  void createThenParse_returnsClaims() {
    Clock clock = Clock.fixed(Instant.parse("2026-05-01T12:00:00Z"), ZoneOffset.UTC);
    JwtProperties props =
        new JwtProperties(TEST_SECRET, Duration.ofMinutes(15), Duration.ofDays(7));
    JwtAccessTokenService service = new JwtAccessTokenService(props, clock);

    String token = service.createAccessToken(42L, "learner@example.com", "STUDENT");
    Optional<AccessTokenClaims> parsed = service.parseValid(token);

    assertThat(parsed)
        .contains(new AccessTokenClaims(42L, "learner@example.com", "STUDENT"));
  }

  @Test
  @DisplayName("parseValid returns empty when token is expired")
  void parseValid_whenExpired_returnsEmpty() {
    SteppableClock clock = new SteppableClock(Instant.parse("2026-05-01T12:00:00Z"));
    JwtProperties props =
        new JwtProperties(TEST_SECRET, Duration.ofNanos(1), Duration.ofDays(7));
    JwtAccessTokenService service = new JwtAccessTokenService(props, clock);

    String token = service.createAccessToken(1L, "a@b.com", "TEACHER");
    clock.advance(Duration.ofHours(1));

    assertThat(service.parseValid(token)).isEmpty();
  }

  @Test
  @DisplayName("parseValid returns empty when signature does not match")
  void parseValid_whenWrongSecret_returnsEmpty() {
    Clock clock = Clock.fixed(Instant.parse("2026-05-01T12:00:00Z"), ZoneOffset.UTC);
    JwtAccessTokenService signer =
        new JwtAccessTokenService(
            new JwtProperties(TEST_SECRET, Duration.ofMinutes(5), Duration.ofDays(7)), clock);
    JwtAccessTokenService otherVerifier =
        new JwtAccessTokenService(
            new JwtProperties("abcdefghijklmnopqrstuvwxyz012345", Duration.ofMinutes(5), Duration.ofDays(7)),
            clock);

    String token = signer.createAccessToken(9L, "x@y.com", "STUDENT");

    assertThat(otherVerifier.parseValid(token)).isEmpty();
  }

  @Test
  @DisplayName("parseValid returns empty for malformed token string")
  void parseValid_whenMalformed_returnsEmpty() {
    Clock clock = Clock.fixed(Instant.parse("2026-05-01T12:00:00Z"), ZoneOffset.UTC);
    JwtAccessTokenService service =
        new JwtAccessTokenService(
            new JwtProperties(TEST_SECRET, Duration.ofMinutes(5), Duration.ofDays(7)), clock);

    assertThat(service.parseValid("not-a-jwt")).isEmpty();
  }

  /** Clock whose instant can be moved forward for expiry tests. */
  private static final class SteppableClock extends Clock {

    private final ZoneOffset zone = ZoneOffset.UTC;
    private Instant now;

    SteppableClock(Instant start) {
      this.now = start;
    }

    void advance(Duration delta) {
      now = now.plus(delta);
    }

    @Override
    public Instant instant() {
      return now;
    }

    @Override
    public ZoneOffset getZone() {
      return zone;
    }

    @Override
    public Clock withZone(java.time.ZoneId zone) {
      throw new UnsupportedOperationException();
    }
  }
}
