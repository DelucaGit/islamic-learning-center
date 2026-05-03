package com.islamiclearningcenter.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.islamiclearningcenter.config.JwtProperties;
import com.islamiclearningcenter.domain.RefreshToken;
import com.islamiclearningcenter.domain.User;
import com.islamiclearningcenter.repository.RefreshTokenRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

  private static final String TEST_SECRET = "01234567890123456789012345678901";

  @Mock private RefreshTokenRepository refreshTokenRepository;

  @Test
  @DisplayName("issueRefreshToken saves SHA-256 fingerprint and returns the opaque token")
  void issueRefreshToken_savesFingerprintAndReturnsPlain() {
    Clock clock = Clock.fixed(Instant.parse("2026-05-02T10:00:00Z"), ZoneOffset.UTC);
    JwtProperties props =
        new JwtProperties(TEST_SECRET, Duration.ofMinutes(15), Duration.ofDays(7));
    Supplier<String> generator = () -> "unit-test-opaque-token-value!!";
    RefreshTokenService service =
        new RefreshTokenService(refreshTokenRepository, clock, props, generator);

    User user = mock(User.class);

    when(refreshTokenRepository.save(any(RefreshToken.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    String plain = service.issueRefreshToken(user);

    assertThat(plain).isEqualTo("unit-test-opaque-token-value!!");

    ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
    verify(refreshTokenRepository).save(captor.capture());
    RefreshToken saved = captor.getValue();
    assertThat(ReflectionTestUtils.getField(saved, "tokenHash"))
        .isEqualTo(Sha256Hasher.hexDigestUtf8("unit-test-opaque-token-value!!"));
    assertThat(ReflectionTestUtils.getField(saved, "expiresAt"))
        .isEqualTo(Instant.parse("2026-05-09T10:00:00Z"));
    assertThat(ReflectionTestUtils.getField(saved, "revoked")).isEqualTo(false);
    assertThat(ReflectionTestUtils.getField(saved, "user")).isSameAs(user);
  }
}
