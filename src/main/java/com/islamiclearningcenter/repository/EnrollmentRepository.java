package com.islamiclearningcenter.repository;

import com.islamiclearningcenter.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {}
