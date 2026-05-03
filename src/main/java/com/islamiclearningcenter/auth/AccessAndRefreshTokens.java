package com.islamiclearningcenter.auth;

/** Pair returned after successful login: short-lived JWT plus opaque refresh token. */
public record AccessAndRefreshTokens(String accessToken, String refreshToken) {}
