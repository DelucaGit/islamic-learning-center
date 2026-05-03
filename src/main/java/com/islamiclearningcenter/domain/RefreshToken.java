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

@Entity
@Table(name = "refresh_tokens")
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

  public RefreshToken() {}

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public boolean isRevoked() {
    return revoked;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public boolean isUsableAt(Instant now) {
    return !revoked && expiresAt.isAfter(now);
  }

  public void revoke() {
    this.revoked = true;
  }
}
