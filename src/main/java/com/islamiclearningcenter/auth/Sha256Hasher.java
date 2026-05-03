package com.islamiclearningcenter.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** SHA-256 over UTF-8 bytes; used to store opaque refresh token fingerprints in the database. */
public final class Sha256Hasher {

  private Sha256Hasher() {}

  public static String hexDigestUtf8(String value) {
    if (value == null) {
      throw new IllegalArgumentException("value must not be null");
    }
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 must be available in the JRE", e);
    }
  }
}
