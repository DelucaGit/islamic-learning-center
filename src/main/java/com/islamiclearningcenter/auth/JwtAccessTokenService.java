package com.islamiclearningcenter.auth;

import com.islamiclearningcenter.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtAccessTokenService {

  private final JwtProperties properties;
  private final Clock clock;

  public JwtAccessTokenService(JwtProperties properties, Clock clock) {
    this.properties = properties;
    this.clock = clock;
  }

  /** Builds a signed HS256 JWT with subject = user id and standard time claims. */
  public String createAccessToken(long userId, String email, String roleName) {
    Instant now = clock.instant();
    Instant exp = now.plus(properties.accessTokenTtl());
    return Jwts.builder()
        .subject(Long.toString(userId))
        .claim("email", email)
        .claim("role", roleName)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(signingKey())
        .compact();
  }

  /**
   * Verifies signature, expiry, and shape. Returns empty if the token is invalid, expired, or
   * malformed.
   */
  public Optional<AccessTokenClaims> parseValid(String compactJwt) {
    try {
      Claims claims =
          Jwts.parser()
              .verifyWith(signingKey())
              .clock(() -> Date.from(clock.instant()))
              .build()
              .parseSignedClaims(compactJwt)
              .getPayload();
      long userId = Long.parseLong(claims.getSubject());
      String email = claims.get("email", String.class);
      String role = claims.get("role", String.class);
      if (email == null || email.isBlank() || role == null || role.isBlank()) {
        return Optional.empty();
      }
      return Optional.of(new AccessTokenClaims(userId, email, role));
    } catch (JwtException | IllegalArgumentException ignored) {
      return Optional.empty();
    }
  }

  private SecretKey signingKey() {
    return Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
  }
}
