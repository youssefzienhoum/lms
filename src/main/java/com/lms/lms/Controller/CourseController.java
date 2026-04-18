package com.lms.lms.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.lms.DTOS.AttemptResultResponse;
import com.lms.lms.DTOS.CourseDTO;
import com.lms.lms.DTOS.CourseRequestDto;
import com.lms.lms.DTOS.CourseResponseDto;
import com.lms.lms.DTOS.EnrollmentResponse;
import com.lms.lms.DTOS.ExamResponse;
import com.lms.lms.DTOS.ExamSubmissionRequest;
import com.lms.lms.DTOS.QuizResponse;
import com.lms.lms.DTOS.QuizSubmissionRequest;
import com.lms.lms.DTOS.StudentProgressResponse;
import com.lms.lms.Entity.User;
import com.lms.lms.ServiceAbstraction.ICourseService;
import com.lms.lms.Services.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courses")

@RequiredArgsConstructor
public class CourseController {

    private final ICourseService courseService;

    @PostMapping("/Createcourses")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponseDto> createCourse(
            @RequestBody CourseRequestDto dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(dto, currentUser.getId()));
    }

    @PutMapping("/{courseId}")
        @PreAuthorize("hasRole('INSTRUCTOR')")

    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable Long courseId,
            @RequestBody CourseRequestDto dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, dto, currentUser.getId()));
    }

    @DeleteMapping("/{courseId}")
        @PreAuthorize("hasRole('INSTRUCTOR')")

    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser) {
        courseService.deleteCourse(courseId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('INSTRUCTOR')")

    public ResponseEntity<List<CourseResponseDto>> getMycourses(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(courseService.getInstructorCourses(currentUser.getId()));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDto> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    //=================
    // Student related
    //=================

    // Browse all available courses
    @GetMapping("/courses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseDTO>> browseAvailableCourses() {
        return ResponseEntity.ok(((CourseService) courseService).browseAvailableCourses());
    }

    // Search courses by name / category
    @GetMapping("/courses/search")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseDTO>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(((CourseService) courseService).searchCourses(keyword, categoryId));
    }

    // Enroll in a course
    @PostMapping("/courses/{courseId}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(((CourseService) courseService).enrollInCourse(courseId));
    }

    // View enrolled courses
    @GetMapping("/courses/enrolled")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> getEnrolledCourses() {
        return ResponseEntity.ok(((CourseService) courseService).getEnrolledCourses());
    }

    // Watch video lectures
    @GetMapping("/courses/{courseId}/lessons/{lessonId}/watch")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> watchLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        return ResponseEntity.ok(((CourseService) courseService).watchLesson(courseId, lessonId));
    }

    // Take lesson quiz (fetch)
    @GetMapping("/courses/{courseId}/lessons/{lessonId}/quiz")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizResponse> getLessonQuiz(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        return ResponseEntity.ok(((CourseService) courseService).getLessonQuiz(courseId, lessonId));
    }

    // Take lesson quiz (submit)
    @PostMapping("/courses/{courseId}/quiz/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AttemptResultResponse> submitLessonQuiz(
            @PathVariable Long courseId,
            @RequestBody QuizSubmissionRequest request) {
        return ResponseEntity.ok(((CourseService) courseService).submitLessonQuiz(courseId, request));
    }

    // Take final course exam (fetch)
    @GetMapping("/courses/{courseId}/exam")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ExamResponse> getCourseExam(@PathVariable Long courseId) {
        return ResponseEntity.ok(((CourseService) courseService).getCourseExam(courseId));
    }

    // Take final course exam (submit)
    @PostMapping("/courses/{courseId}/exam/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AttemptResultResponse> submitCourseExam(
            @PathVariable Long courseId,
            @RequestBody ExamSubmissionRequest request) {
        return ResponseEntity.ok(((CourseService) courseService).submitCourseExam(request));
    }

    // View grades and progress
    @GetMapping("/courses/{courseId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentProgressResponse> getStudentProgress(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(((CourseService) courseService).getStudentProgress(courseId));
    }

    // Mark lesson as completed (helper method for the calculation of the course progress)
    @PostMapping("/courses/{courseId}/lessons/{lessonId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Double> markLessonAsCompleted(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        double progress = ((CourseService) courseService).markLessonAsCompleted(courseId, lessonId);
        return ResponseEntity.ok(progress);
    }
}