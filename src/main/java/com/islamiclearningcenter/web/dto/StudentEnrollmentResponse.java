package com.islamiclearningcenter.web.dto;

import com.islamiclearningcenter.domain.Enrollment;
import java.time.Instant;

public record StudentEnrollmentResponse(
    long enrollmentId,
    long courseId,
    String courseTitle,
    long teacherId,
    Instant createdAt) {

  public static StudentEnrollmentResponse from(Enrollment enrollment) {
    return new StudentEnrollmentResponse(
        enrollment.getId(),
        enrollment.getCourse().getId(),
        enrollment.getCourse().getTitle(),
        enrollment.getCourse().getTeacher().getId(),
        enrollment.getCreatedAt());
  }
}
