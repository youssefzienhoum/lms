package com.lms.lms.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lms.lms.DTOS.CourseRequestDto;
import com.lms.lms.DTOS.CourseResponseDto;
import com.lms.lms.Entity.User;
import com.lms.lms.ServiceAbstraction.ICourseService;
// import com.lms.lms.Services.CourseService;

import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
public class CourseController {

    private final ICourseService courseService;

    @PostMapping("/Createcourses")
    public ResponseEntity<CourseResponseDto> createCourse(
            @RequestBody CourseRequestDto dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(dto, currentUser.getId()));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable Long courseId,
            @RequestBody CourseRequestDto dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, dto, currentUser.getId()));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser) {
        courseService.deleteCourse(courseId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseResponseDto>> getMycourses(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(courseService.getInstructorCourses(currentUser.getId()));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDto> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }
    
}
