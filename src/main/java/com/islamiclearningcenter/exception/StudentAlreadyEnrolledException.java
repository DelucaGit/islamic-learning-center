package com.islamiclearningcenter.exception;

public class StudentAlreadyEnrolledException extends RuntimeException {

  public StudentAlreadyEnrolledException(long courseId, long studentId) {
    super("Student " + studentId + " is already enrolled in course " + courseId);
  }
}
