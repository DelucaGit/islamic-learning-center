package com.islamiclearningcenter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.islamiclearningcenter.domain.Course;
import com.islamiclearningcenter.domain.User;
import com.islamiclearningcenter.domain.UserRole;
import com.islamiclearningcenter.exception.ForbiddenException;
import com.islamiclearningcenter.exception.NotFoundException;
import com.islamiclearningcenter.repository.CourseRepository;
import com.islamiclearningcenter.repository.UserRepository;
import com.islamiclearningcenter.web.dto.CreateCourseRequest;
import com.islamiclearningcenter.web.dto.UpdateCourseRequest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

  @Mock private CourseRepository courseRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private CourseService courseService;

  @Test
  @DisplayName("create persists course with teacher from caller id")
  void create_success() {
    User teacherRef = new User();
    teacherRef.setId(5L);
    when(userRepository.getReferenceById(5L)).thenReturn(teacherRef);
    when(courseRepository.save(any(Course.class)))
        .thenAnswer(
            invocation -> {
              Course c = invocation.getArgument(0);
              c.setId(99L);
              return c;
            });

    var response =
        courseService.create(5L, new CreateCourseRequest("  Title  ", "  desc  ", true));

    assertThat(response.id()).isEqualTo(99L);
    assertThat(response.teacherId()).isEqualTo(5L);
    assertThat(response.title()).isEqualTo("Title");
    assertThat(response.description()).isEqualTo("desc");
    assertThat(response.active()).isTrue();

    ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
    verify(courseRepository).save(captor.capture());
    assertThat(captor.getValue().getTeacher()).isSameAs(teacherRef);
  }

  @Test
  @DisplayName("getById throws when course missing")
  void getById_notFound() {
    when(courseRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> courseService.getById(1L)).isInstanceOf(NotFoundException.class);
  }

  @Test
  @DisplayName("update throws ForbiddenException when caller is not owner")
  void update_wrongOwner() {
    User owner = new User();
    owner.setId(1L);
    Course course = new Course();
    course.setId(10L);
    course.setTeacher(owner);
    when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

    assertThatThrownBy(
            () ->
                courseService.update(
                    10L, 2L, new UpdateCourseRequest("New title", null, null)))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  @DisplayName("listActiveCourses maps repository results")
  void listActive() {
    User teacher = new User();
    teacher.setId(3L);
    Course c = new Course();
    c.setId(7L);
    c.setTeacher(teacher);
    c.setTitle("T");
    c.setDescription(null);
    c.setActive(true);
    c.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
    c.setUpdatedAt(Instant.parse("2026-01-02T00:00:00Z"));
    when(courseRepository.findAllByActiveTrueOrderByCreatedAtDesc()).thenReturn(List.of(c));

    var list = courseService.listActiveCourses();

    assertThat(list).hasSize(1);
    assertThat(list.get(0).id()).isEqualTo(7L);
    assertThat(list.get(0).title()).isEqualTo("T");
  }
}
