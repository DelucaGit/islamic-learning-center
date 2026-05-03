package com.islamiclearningcenter.repository;

import com.islamiclearningcenter.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
}
