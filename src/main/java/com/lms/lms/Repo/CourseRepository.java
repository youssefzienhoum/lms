package com.lms.lms.Repo;

import com.lms.lms.Entity.Course;
import com.lms.lms.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
 
    
    // Instructor courses
    List<Course> findByInstructorAndPublishedTrueOrderByCreatedAtDesc(User instructor);
    
    // Search + Filter
    List<Course> findByTitleContainingIgnoreCaseAndFreeTrueAndPublishedTrue(String title);
    List<Course> findByCategoryIdAndFreeTrueAndPublishedTrue(Long categoryId);
    

}