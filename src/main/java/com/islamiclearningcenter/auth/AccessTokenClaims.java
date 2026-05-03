package com.islamiclearningcenter.auth;

/** Values extracted from a valid signed access JWT (issuer is this application). */
public record AccessTokenClaims(long userId, String email, String roleName) {}
