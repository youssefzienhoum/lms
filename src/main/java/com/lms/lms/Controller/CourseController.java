package com.lms.lms.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.lms.DTOS.CourseRequestDto;
import com.lms.lms.DTOS.CourseResponseDto;
import com.lms.lms.Entity.User;
import com.lms.lms.ServiceAbstraction.ICourseService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/courses")

@RequiredArgsConstructor
public class CourseController {

    private final ICourseService courseService;
    private final com.lms.lms.Repo.UserRepository userRepository;

    @PostMapping("/createcourses/{instructorId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponseDto> createCourse(
        @RequestBody CourseRequestDto dto,
        @PathVariable Long instructorId) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(courseService.createCourse(dto, instructorId));
    }

    @PutMapping("/updatecourses/{courseId}/{currentUser}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable Long courseId,
            @RequestBody CourseRequestDto dto,
            @PathVariable Long currentUser) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, dto, currentUser));
    }

    @DeleteMapping("/{courseId}/{currentUser}")
    @PreAuthorize("hasRole('INSTRUCTOR')")

    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId,
            @PathVariable Long currentUser) {
        courseService.deleteCourse(courseId, currentUser);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/my-courses/{currentUser}")
    @PreAuthorize("hasRole('INSTRUCTOR')")

    public ResponseEntity<List<CourseResponseDto>> getMycourses(
            @PathVariable Long currentUser) {
        return ResponseEntity.ok(courseService.getInstructorCourses(currentUser));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDto> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }
    
}
