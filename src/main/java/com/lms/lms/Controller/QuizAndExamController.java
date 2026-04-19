package com.lms.lms.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.lms.DTOS.AttemptResultResponse;
import com.lms.lms.DTOS.ExamResponse;
import com.lms.lms.DTOS.ExamSubmissionRequest;
import com.lms.lms.DTOS.QuizResponse;
import com.lms.lms.DTOS.QuizSubmissionRequest;
import com.lms.lms.DTOS.StudentProgressResponse;
import com.lms.lms.Services.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/quizzes-exams")
@RequiredArgsConstructor

public class QuizAndExamController {
        private final CourseService courseService;
        

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
