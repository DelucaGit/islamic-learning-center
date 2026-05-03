package com.islamiclearningcenter.service;

import com.islamiclearningcenter.domain.Course;
import com.islamiclearningcenter.domain.Enrollment;
import com.islamiclearningcenter.domain.User;
import com.islamiclearningcenter.domain.UserRole;
import com.islamiclearningcenter.exception.ForbiddenException;
import com.islamiclearningcenter.exception.NotFoundException;
import com.islamiclearningcenter.exception.StudentAlreadyEnrolledException;
import com.islamiclearningcenter.repository.CourseRepository;
import com.islamiclearningcenter.repository.EnrollmentRepository;
import com.islamiclearningcenter.repository.UserRepository;
import com.islamiclearningcenter.web.dto.AddStudentRequest;
import com.islamiclearningcenter.web.dto.EnrolledStudentResponse;
import com.islamiclearningcenter.web.dto.StudentEnrollmentResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService {

  private final EnrollmentRepository enrollmentRepository;
  private final CourseRepository courseRepository;
  private final UserRepository userRepository;

  public EnrollmentService(
      EnrollmentRepository enrollmentRepository,
      CourseRepository courseRepository,
      UserRepository userRepository) {
    this.enrollmentRepository = enrollmentRepository;
    this.courseRepository = courseRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<EnrolledStudentResponse> listStudents(long courseId, long teacherId) {
    requireOwnedCourse(courseId, teacherId);
    return enrollmentRepository.findAllByCourse_IdOrderByCreatedAtAsc(courseId).stream()
        .map(EnrolledStudentResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<StudentEnrollmentResponse> listEnrollmentsForStudent(long studentId) {
    return enrollmentRepository.findAllByStudent_IdOrderByCreatedAtDesc(studentId).stream()
        .map(StudentEnrollmentResponse::from)
        .toList();
  }

  @Transactional
  public EnrolledStudentResponse enrollStudent(
      long courseId, long teacherId, AddStudentRequest request) {
    Course course = requireOwnedCourse(courseId, teacherId);
    User student = resolveStudent(request);
    if (student.getRole() != UserRole.STUDENT) {
      throw new IllegalArgumentException("User must have role STUDENT");
    }
    if (enrollmentRepository.existsByCourse_IdAndStudent_Id(courseId, student.getId())) {
      throw new StudentAlreadyEnrolledException(courseId, student.getId());
    }
    Enrollment enrollment = new Enrollment();
    enrollment.setCourse(course);
    enrollment.setStudent(student);
    enrollment.setCreatedAt(Instant.now());
    return EnrolledStudentResponse.from(enrollmentRepository.save(enrollment));
  }

  @Transactional
  public void unenrollStudent(long courseId, long teacherId, long studentId) {
    requireOwnedCourse(courseId, teacherId);
    int deleted =
        enrollmentRepository.deleteByCourseIdAndStudentId(courseId, studentId);
    if (deleted == 0) {
      throw new NotFoundException("Enrollment not found for this course and student");
    }
  }

  private User resolveStudent(AddStudentRequest request) {
    if (request.studentId() != null) {
      return userRepository
          .findById(request.studentId())
          .orElseThrow(() -> new IllegalArgumentException("No user found with this id"));
    }
    String normalized = RegistrationService.normalizeEmail(request.email());
    return userRepository
        .findByEmail(normalized)
        .orElseThrow(() -> new IllegalArgumentException("No user found with this email"));
  }

  private Course requireOwnedCourse(long courseId, long teacherId) {
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));
    if (course.getTeacher().getId() != teacherId) {
      throw new ForbiddenException("You do not own this course");
    }
    return course;
  }
}
