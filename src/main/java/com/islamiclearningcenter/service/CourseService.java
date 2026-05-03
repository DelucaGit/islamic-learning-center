package com.islamiclearningcenter.service;

import com.islamiclearningcenter.domain.Course;
import com.islamiclearningcenter.domain.User;
import com.islamiclearningcenter.exception.ForbiddenException;
import com.islamiclearningcenter.exception.NotFoundException;
import com.islamiclearningcenter.repository.CourseRepository;
import com.islamiclearningcenter.repository.UserRepository;
import com.islamiclearningcenter.web.dto.CourseResponse;
import com.islamiclearningcenter.web.dto.CreateCourseRequest;
import com.islamiclearningcenter.web.dto.UpdateCourseRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

  private final CourseRepository courseRepository;
  private final UserRepository userRepository;

  public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
    this.courseRepository = courseRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<CourseResponse> listActiveCourses() {
    return courseRepository.findAllByActiveTrueOrderByCreatedAtDesc().stream()
        .map(CourseResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<CourseResponse> listOwnedBy(long teacherId) {
    return courseRepository.findAllByTeacher_IdOrderByCreatedAtDesc(teacherId).stream()
        .map(CourseResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public CourseResponse getById(long courseId) {
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));
    return CourseResponse.from(course);
  }

  @Transactional
  public CourseResponse create(long teacherId, CreateCourseRequest request) {
    User teacher = userRepository.getReferenceById(teacherId);
    Course course = new Course();
    course.setTeacher(teacher);
    course.setTitle(request.title().trim());
    course.setDescription(normalizeDescription(request.description()));
    boolean active = request.active() == null || request.active();
    course.setActive(active);
    Instant now = Instant.now();
    course.setCreatedAt(now);
    course.setUpdatedAt(now);
    return CourseResponse.from(courseRepository.save(course));
  }

  @Transactional
  public CourseResponse update(long courseId, long teacherId, UpdateCourseRequest request) {
    Course course = loadOwnedOrThrow(courseId, teacherId);
    boolean changed = false;
    if (request.title() != null) {
      String trimmed = request.title().trim();
      if (trimmed.isEmpty()) {
        throw new IllegalArgumentException("title must not be blank");
      }
      course.setTitle(trimmed);
      changed = true;
    }
    if (request.description() != null) {
      course.setDescription(normalizeDescription(request.description()));
      changed = true;
    }
    if (request.active() != null) {
      course.setActive(request.active());
      changed = true;
    }
    if (changed) {
      course.setUpdatedAt(Instant.now());
      courseRepository.save(course);
    }
    return CourseResponse.from(course);
  }

  @Transactional
  public void delete(long courseId, long teacherId) {
    Course course = loadOwnedOrThrow(courseId, teacherId);
    courseRepository.delete(course);
  }

  private Course loadOwnedOrThrow(long courseId, long teacherId) {
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));
    if (course.getTeacher().getId() != teacherId) {
      throw new ForbiddenException("You do not own this course");
    }
    return course;
  }

  private static String normalizeDescription(String description) {
    if (description == null) {
      return null;
    }
    String trimmed = description.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
