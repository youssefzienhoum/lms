
package com.lms.lms.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "is_published")
    private boolean published = false;
    
    @Column(name = "total_lessons")
    private Integer totalLessons = 0;
    
    @Column(name = "total_duration")
    private Integer totalDuration = 0;
    
    @Enumerated(EnumType.STRING)
    private Level level;
    
    // Relationships
    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();
    
    public enum Level {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}
