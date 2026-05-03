package com.islamiclearningcenter.web.dto;

import com.islamiclearningcenter.domain.Course;
import java.time.Instant;

public record CourseResponse(
    long id,
    long teacherId,
    String title,
    String description,
    boolean active,
    Instant createdAt,
    Instant updatedAt) {

  public static CourseResponse from(Course course) {
    return new CourseResponse(
        course.getId(),
        course.getTeacher().getId(),
        course.getTitle(),
        course.getDescription(),
        course.isActive(),
        course.getCreatedAt(),
        course.getUpdatedAt());
  }
}
