package com.islamiclearningcenter.repository;

import com.islamiclearningcenter.domain.Course;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

  @EntityGraph(attributePaths = "teacher")
  List<Course> findAllByTeacher_IdOrderByCreatedAtDesc(Long teacherId);

  @EntityGraph(attributePaths = "teacher")
  List<Course> findAllByActiveTrueOrderByCreatedAtDesc();
}
