package com.islamiclearningcenter.auth;

import com.islamiclearningcenter.domain.UserRole;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/** Reads the authenticated principal set by {@link JwtAuthenticationFilter}. */
public final class CurrentUser {

  private CurrentUser() {}

  public record AuthPrincipal(long userId, UserRole role, String email) {}

  public static AuthPrincipal current() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("Unauthenticated");
    }
    Object details = authentication.getDetails();
    if (!(details instanceof Long)) {
      throw new IllegalStateException("Expected JWT user id on authentication details");
    }
    long userId = (Long) details;
    UserRole role =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(a -> a.startsWith("ROLE_"))
            .map(a -> a.substring("ROLE_".length()))
            .findFirst()
            .map(UserRole::valueOf)
            .orElseThrow(() -> new IllegalStateException("Missing role authority"));
    String email = Objects.requireNonNullElse(authentication.getName(), "");
    return new AuthPrincipal(userId, role, email);
  }
}
