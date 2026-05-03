package com.islamiclearningcenter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

  @Mock private EnrollmentRepository enrollmentRepository;

  @Mock private CourseRepository courseRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private EnrollmentService enrollmentService;

  @Test
  @DisplayName("enrollStudent saves when teacher owns course and user is STUDENT")
  void enrollStudent_success() {
    User owner = new User();
    owner.setId(1L);
    Course course = new Course();
    course.setId(10L);
    course.setTeacher(owner);
    when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

    User student = new User();
    student.setId(20L);
    student.setEmail("s@test.com");
    student.setFullName("Student");
    student.setRole(UserRole.STUDENT);
    when(userRepository.findById(20L)).thenReturn(Optional.of(student));
    when(enrollmentRepository.existsByCourse_IdAndStudent_Id(10L, 20L)).thenReturn(false);
    when(enrollmentRepository.save(any(Enrollment.class)))
        .thenAnswer(
            inv -> {
              Enrollment e = inv.getArgument(0);
              e.setId(100L);
              return e;
            });

    var res =
        enrollmentService.enrollStudent(10L, 1L, new AddStudentRequest(20L, null));

    assertThat(res.enrollmentId()).isEqualTo(100L);
    assertThat(res.courseId()).isEqualTo(10L);
    assertThat(res.studentId()).isEqualTo(20L);
    verify(enrollmentRepository).save(any(Enrollment.class));
  }

  @Test
  @DisplayName("enrollStudent throws StudentAlreadyEnrolledException when duplicate")
  void enrollStudent_duplicate() {
    User owner = new User();
    owner.setId(1L);
    Course course = new Course();
    course.setId(10L);
    course.setTeacher(owner);
    when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

    User student = new User();
    student.setId(20L);
    student.setRole(UserRole.STUDENT);
    when(userRepository.findById(20L)).thenReturn(Optional.of(student));
    when(enrollmentRepository.existsByCourse_IdAndStudent_Id(10L, 20L)).thenReturn(true);

    assertThatThrownBy(
            () -> enrollmentService.enrollStudent(10L, 1L, new AddStudentRequest(20L, null)))
        .isInstanceOf(StudentAlreadyEnrolledException.class);
  }

  @Test
  @DisplayName("enrollStudent throws IllegalArgumentException when user is not STUDENT")
  void enrollStudent_notStudent() {
    User owner = new User();
    owner.setId(1L);
    Course course = new Course();
    course.setId(10L);
    course.setTeacher(owner);
    when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

    User teacherUser = new User();
    teacherUser.setId(20L);
    teacherUser.setRole(UserRole.TEACHER);
    when(userRepository.findById(20L)).thenReturn(Optional.of(teacherUser));

    assertThatThrownBy(
            () -> enrollmentService.enrollStudent(10L, 1L, new AddStudentRequest(20L, null)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("STUDENT");
  }

  @Test
  @DisplayName("enrollStudent throws ForbiddenException when caller does not own course")
  void enrollStudent_forbidden() {
    User owner = new User();
    owner.setId(1L);
    Course course = new Course();
    course.setId(10L);
    course.setTeacher(owner);
    when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

    assertThatThrownBy(
            () -> enrollmentService.enrollStudent(10L, 99L, new AddStudentRequest(20L, null)))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  @DisplayName("unenrollStudent throws NotFoundException when no row deleted")
  void unenrollStudent_notFound() {
    User owner = new User();
    owner.setId(1L);
    Course course = new Course();
    course.setId(10L);
    course.setTeacher(owner);
    when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
    when(enrollmentRepository.deleteByCourseIdAndStudentId(10L, 20L)).thenReturn(0);

    assertThatThrownBy(() -> enrollmentService.unenrollStudent(10L, 1L, 20L))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  @DisplayName("enrollStudent throws NotFoundException when course missing")
  void enrollStudent_courseMissing() {
    when(courseRepository.findById(10L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> enrollmentService.enrollStudent(10L, 1L, new AddStudentRequest(20L, null)))
        .isInstanceOf(NotFoundException.class);
  }
}
