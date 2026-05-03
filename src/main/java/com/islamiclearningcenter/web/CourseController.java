package com.islamiclearningcenter.web;

import com.islamiclearningcenter.auth.CurrentUser;
import com.islamiclearningcenter.service.CourseService;
import com.islamiclearningcenter.web.dto.CourseResponse;
import com.islamiclearningcenter.web.dto.CreateCourseRequest;
import com.islamiclearningcenter.web.dto.UpdateCourseRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

  private final CourseService courseService;

  public CourseController(CourseService courseService) {
    this.courseService = courseService;
  }

  @GetMapping
  public List<CourseResponse> listActive() {
    return courseService.listActiveCourses();
  }

  @GetMapping("/{id}")
  public CourseResponse getById(@PathVariable("id") long id) {
    return courseService.getById(id);
  }

  @PostMapping
  @PreAuthorize("hasRole('TEACHER')")
  @ResponseStatus(HttpStatus.CREATED)
  public CourseResponse create(@Valid @RequestBody CreateCourseRequest request) {
    return courseService.create(CurrentUser.current().userId(), request);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('TEACHER')")
  public CourseResponse update(
      @PathVariable("id") long id, @Valid @RequestBody UpdateCourseRequest request) {
    return courseService.update(id, CurrentUser.current().userId(), request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('TEACHER')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") long id) {
    courseService.delete(id, CurrentUser.current().userId());
  }
}
