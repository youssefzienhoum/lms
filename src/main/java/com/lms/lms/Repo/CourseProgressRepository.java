package com.lms.lms.Repo;


import com.lms.lms.Entity.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {
    List<CourseProgress> findByEnrollmentStudentIdAndEnrollmentCourseId(Long studentId, Long courseId);
    
    @Query("SELECT AVG(cp.progressPercentage) FROM CourseProgress cp " +
           "WHERE cp.enrollment.student.id = ?1 AND cp.enrollment.course.id = ?2")
    Double getCourseProgressAverage(Long studentId, Long courseId);
    
    Optional<CourseProgress> findByEnrollmentStudentIdAndEnrollmentCourseIdAndLessonId(
        Long studentId, Long courseId, Long lessonId);
}