package com.islamiclearningcenter.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtAccessTokenService jwtAccessTokenService;

  public JwtAuthenticationFilter(JwtAccessTokenService jwtAccessTokenService) {
    this.jwtAccessTokenService = jwtAccessTokenService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String method = request.getMethod();
    String uri = request.getRequestURI();
    if (uri.startsWith("/api/v1/auth/")) {
      return true;
    }
    if ("GET".equals(method) && "/api/v1/health".equals(uri)) {
      return true;
    }
    return "GET".equals(method) && uri.startsWith("/actuator/health");
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    String compactJwt = header.substring(7).trim();
    Optional<AccessTokenClaims> parsed = jwtAccessTokenService.parseValid(compactJwt);
    if (parsed.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }
    AccessTokenClaims claims = parsed.get();
    var authorities =
        List.of(new SimpleGrantedAuthority("ROLE_" + claims.roleName()));
    var authentication =
        new UsernamePasswordAuthenticationToken(claims.email(), null, authorities);
    authentication.setDetails(claims.userId());
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }
}
