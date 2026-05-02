package com.islamiclearningcenter.repository;

import com.islamiclearningcenter.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {}
