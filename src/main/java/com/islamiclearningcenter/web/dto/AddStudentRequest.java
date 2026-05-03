package com.islamiclearningcenter.web.dto;

import jakarta.validation.constraints.AssertTrue;

public record AddStudentRequest(Long studentId, String email) {

  @AssertTrue(message = "Exactly one of studentId or email must be provided")
  public boolean isExactlyOneLookup() {
    boolean hasId = studentId != null;
    boolean hasEmail = email != null && !email.isBlank();
    return hasId != hasEmail;
  }
}
