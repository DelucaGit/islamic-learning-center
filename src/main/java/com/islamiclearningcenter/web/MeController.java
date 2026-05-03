package com.islamiclearningcenter.web;

import com.islamiclearningcenter.auth.CurrentUser;
import com.islamiclearningcenter.service.CourseService;
import com.islamiclearningcenter.service.EnrollmentService;
import com.islamiclearningcenter.web.dto.CourseResponse;
import com.islamiclearningcenter.web.dto.StudentEnrollmentResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {

  private final CourseService courseService;
  private final EnrollmentService enrollmentService;

  public MeController(CourseService courseService, EnrollmentService enrollmentService) {
    this.courseService = courseService;
    this.enrollmentService = enrollmentService;
  }

  @GetMapping("/courses")
  @PreAuthorize("hasRole('TEACHER')")
  public List<CourseResponse> myCourses() {
    return courseService.listOwnedBy(CurrentUser.current().userId());
  }

  @GetMapping("/enrollments")
  @PreAuthorize("hasRole('STUDENT')")
  public List<StudentEnrollmentResponse> myEnrollments() {
    return enrollmentService.listEnrollmentsForStudent(CurrentUser.current().userId());
  }
}
