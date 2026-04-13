package com.lms.lms.Repo;


import com.lms.lms.Entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseIdOrderByLessonOrder(Long courseId);
    
    @Query("SELECT l FROM Lesson l WHERE l.course.id = ?1 AND l.preview = true")
    List<Lesson> findPreviewLessonsByCourseId(Long courseId);
}