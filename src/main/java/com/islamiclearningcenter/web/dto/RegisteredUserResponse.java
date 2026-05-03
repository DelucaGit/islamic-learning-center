package com.islamiclearningcenter.web.dto;

import com.islamiclearningcenter.domain.User;
import com.islamiclearningcenter.domain.UserRole;

public record RegisteredUserResponse(long id, String email, String fullName, UserRole role) {

  public static RegisteredUserResponse fromEntity(User user) {
    return new RegisteredUserResponse(
        user.getId(), user.getEmail(), user.getFullName(), user.getRole());
  }
}
