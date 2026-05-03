package com.islamiclearningcenter.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "token_hash", nullable = false)
  private String tokenHash;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(nullable = false)
  private boolean revoked = false;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  /** New row; {@code tokenHash} must be a fingerprint of the opaque token (never store the raw token). */
  public static RefreshToken forUser(User user, String tokenHash, Instant expiresAt) {
    RefreshToken row = new RefreshToken();
    row.user = user;
    row.tokenHash = tokenHash;
    row.expiresAt = expiresAt;
    row.revoked = false;
    return row;
  }
}
