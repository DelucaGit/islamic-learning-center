package com.islamiclearningcenter.web;

import com.islamiclearningcenter.auth.CurrentUser;
import com.islamiclearningcenter.service.EnrollmentService;
import com.islamiclearningcenter.web.dto.AddStudentRequest;
import com.islamiclearningcenter.web.dto.EnrolledStudentResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class EnrollmentController {

  private final EnrollmentService enrollmentService;

  public EnrollmentController(EnrollmentService enrollmentService) {
    this.enrollmentService = enrollmentService;
  }

  @GetMapping("/courses/{courseId}/students")
  @PreAuthorize("hasRole('TEACHER')")
  public List<EnrolledStudentResponse> listStudents(@PathVariable long courseId) {
    return enrollmentService.listStudents(courseId, CurrentUser.current().userId());
  }

  @PostMapping("/courses/{courseId}/students")
  @PreAuthorize("hasRole('TEACHER')")
  @ResponseStatus(HttpStatus.CREATED)
  public EnrolledStudentResponse enroll(
      @PathVariable long courseId, @Valid @RequestBody AddStudentRequest request) {
    return enrollmentService.enrollStudent(courseId, CurrentUser.current().userId(), request);
  }

  @DeleteMapping("/courses/{courseId}/students/{studentId}")
  @PreAuthorize("hasRole('TEACHER')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unenroll(@PathVariable long courseId, @PathVariable long studentId) {
    enrollmentService.unenrollStudent(courseId, CurrentUser.current().userId(), studentId);
  }
}
