package com.lms.lms.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, name = "first_name")
    private String firstName;
    
    @Column(nullable = false, name = "last_name")
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.STUDENT;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    @Column(name = "is_active")
    private boolean active = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments;
    
    @OneToMany(mappedBy = "instructor")
    private List<Course> courses;
    
    public enum Role { STUDENT, INSTRUCTOR, ADMIN }
}