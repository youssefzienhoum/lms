package com.lms.lms.DTOS;

import com.lms.lms.Entity.Enrollment;
import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long enrollmentId,
        Long studentId,
        Long courseId,
        String courseTitle,
        String status,
        LocalDateTime enrolledAt
) {
    public static EnrollmentResponse fromEntity(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudentId(),
                enrollment.getCourseId(),
                enrollment.getCourse().getTitle(),
                enrollment.getStatus().name(),
                enrollment.getEnrolledAt()
        );
    }
}