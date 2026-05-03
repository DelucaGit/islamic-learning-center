package com.islamiclearningcenter.web.dto;

import com.islamiclearningcenter.domain.Enrollment;
import java.time.Instant;

public record EnrolledStudentResponse(
    long enrollmentId,
    long courseId,
    long studentId,
    String studentEmail,
    String studentFullName,
    Instant createdAt) {

  public static EnrolledStudentResponse from(Enrollment enrollment) {
    return new EnrolledStudentResponse(
        enrollment.getId(),
        enrollment.getCourse().getId(),
        enrollment.getStudent().getId(),
        enrollment.getStudent().getEmail(),
        enrollment.getStudent().getFullName(),
        enrollment.getCreatedAt());
  }
}
