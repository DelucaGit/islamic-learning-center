package com.islamiclearningcenter.repository;

import com.islamiclearningcenter.domain.Enrollment;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

  boolean existsByCourse_IdAndStudent_Id(Long courseId, Long studentId);

  @EntityGraph(attributePaths = "student")
  List<Enrollment> findAllByCourse_IdOrderByCreatedAtAsc(Long courseId);

  @EntityGraph(attributePaths = {"course", "course.teacher"})
  List<Enrollment> findAllByStudent_IdOrderByCreatedAtDesc(Long studentId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "DELETE FROM Enrollment e WHERE e.course.id = :courseId AND e.student.id = :studentId")
  int deleteByCourseIdAndStudentId(
      @Param("courseId") Long courseId, @Param("studentId") Long studentId);
}
