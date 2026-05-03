export type UserRole = "TEACHER" | "STUDENT";

export type RegisteredUserResponse = {
  id: number;
  email: string;
  fullName: string;
  role: UserRole;
};

export type TokenPair = {
  accessToken: string;
  refreshToken: string;
};

export type Course = {
  id: number;
  teacherId: number;
  title: string;
  description: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
};

export type EnrolledStudentResponse = {
  enrollmentId: number;
  courseId: number;
  studentId: number;
  studentEmail: string;
  studentFullName: string;
  createdAt: string;
};

export type StudentEnrollmentResponse = {
  enrollmentId: number;
  courseId: number;
  courseTitle: string;
  teacherId: number;
  createdAt: string;
};
