package com.islamiclearningcenter.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Sha256HasherTest {

  @Test
  @DisplayName("hexDigestUtf8 matches known SHA-256 of \"hello\"")
  void hexDigestUtf8_knownVector() {
    assertThat(Sha256Hasher.hexDigestUtf8("hello"))
        .isEqualTo("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824");
  }

  @Test
  @DisplayName("hexDigestUtf8 is stable for the same input")
  void hexDigestUtf8_stable() {
    String once = Sha256Hasher.hexDigestUtf8("opaque-token-value");
    String twice = Sha256Hasher.hexDigestUtf8("opaque-token-value");
    assertThat(once).isEqualTo(twice).hasSize(64);
  }

  @Test
  @DisplayName("hexDigestUtf8 rejects null")
  void hexDigestUtf8_nullRejected() {
    assertThatThrownBy(() -> Sha256Hasher.hexDigestUtf8(null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
