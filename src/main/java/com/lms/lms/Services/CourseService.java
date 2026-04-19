package com.lms.lms.Services;

import com.lms.lms.DTOS.CourseDTO;
import com.lms.lms.DTOS.CourseRequestDto;
import com.lms.lms.DTOS.CourseResponseDto;
import com.lms.lms.DTOS.ExamScoreResponse;
import com.lms.lms.DTOS.QuizScoreResponse;
import com.lms.lms.DTOS.StudentProgressResponse;
import com.lms.lms.Entity.Category;
import com.lms.lms.Entity.Course;
import com.lms.lms.Entity.User;
import com.lms.lms.Repo.CategoryRepository;
import com.lms.lms.Repo.CourseProgressRepository;
import com.lms.lms.Repo.CourseRepository;
import com.lms.lms.Repo.EnrollmentRepository;
import com.lms.lms.Repo.ExamAttemptRepository;
import com.lms.lms.Repo.LessonRepository;
import com.lms.lms.Repo.QuizAttemptRepository;
import com.lms.lms.Repo.UserRepository;
import com.lms.lms.ServiceAbstraction.ICourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor

public class CourseService implements ICourseService {
    @Autowired
    private CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;


    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final CourseProgressRepository courseProgressRepository;

    @Override
    public CourseResponseDto createCourse(CourseRequestDto dto, Long instructorId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        Course course = new Course();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setThumbnailUrl(dto.getThumbnailUrl());
        course.setFree(dto.getFree());
        course.setTotalLessons(dto.getTotalLessons());
        course.setTotalDuration(dto.getTotalDuration());
        course.setInstructor(instructor);
        course.setCategory(category);

        Course saved = courseRepository.save(course);

        // ── inline mapping ──
        CourseResponseDto response = new CourseResponseDto();
        response.setId(saved.getId());
        response.setTitle(saved.getTitle());
        response.setDescription(saved.getDescription());
        response.setThumbnailUrl(saved.getThumbnailUrl());
        // response.setPublished(saved.getPublished());
        response.setTotalLessons(saved.getTotalLessons());
        response.setTotalDuration(saved.getTotalDuration());
        response.setInstructorId(saved.getInstructor().getId());
        response.setCategoryId(saved.getCategory().getId());
        response.setCreatedAt(saved.getCreatedAt());
        return response;

    }
    @Override
    public CourseResponseDto updateCourse(Long courseId, CourseRequestDto dto, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Course not found"));

        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this course");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found"));

        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setThumbnailUrl(dto.getThumbnailUrl());
        course.setFree(dto.getFree());
        course.setTotalLessons(dto.getTotalLessons());
        course.setTotalDuration(dto.getTotalDuration());
        course.setCategory(category);

        Course saved = courseRepository.save(course);

        // ── inline mapping ──
        CourseResponseDto response = new CourseResponseDto();
        response.setId(saved.getId());
        response.setTitle(saved.getTitle());
        response.setDescription(saved.getDescription());
        response.setThumbnailUrl(saved.getThumbnailUrl());
        // response.setIsFree(saved.getIsFree());
        // response.setIsPublished(saved.getIsPublished());
        response.setTotalLessons(saved.getTotalLessons());
        response.setTotalDuration(saved.getTotalDuration());
        response.setInstructorId(saved.getInstructor().getId());
        response.setCategoryId(saved.getCategory().getId());
        response.setCreatedAt(saved.getCreatedAt());
        // response.setUpdatedAt(saved.getUpdatedAt());
        return response;
    }

    @Override
    public void deleteCourse(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Course not found"));

        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this course");
        }

        courseRepository.delete(course);
    }

    @Override
    public List<CourseResponseDto> getInstructorCourses(Long instructorId) {
        List<Course> courses = courseRepository.findByInstructor(instructorId);

        return courses.stream().map(c -> {
            CourseResponseDto response = new CourseResponseDto();
            response.setId(c.getId());
            response.setTitle(c.getTitle());
            response.setDescription(c.getDescription());
            response.setThumbnailUrl(c.getThumbnailUrl());
            // response.setIsFree(c.getFree());
            // response.setIsPublished(c.getIsPublished());
            response.setTotalLessons(c.getTotalLessons());
            response.setTotalDuration(c.getTotalDuration());
            response.setInstructorId(c.getInstructor().getId());
            response.setCategoryId(c.getCategory().getId());
            response.setCreatedAt(c.getCreatedAt());
            // response.setUpdatedAt(c.getUpdatedAt());
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public CourseResponseDto getCourseById(Long courseId) {
        Course c = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Course not found"));

        CourseResponseDto response = new CourseResponseDto();
        response.setId(c.getId());
        response.setTitle(c.getTitle());
        response.setDescription(c.getDescription());
        response.setThumbnailUrl(c.getThumbnailUrl());
        // response.setIsFree(c.getIsFree());
        // response.setIsPublished(c.getIsPublished());
        response.setTotalLessons(c.getTotalLessons());
        response.setTotalDuration(c.getTotalDuration());
        response.setInstructorId(c.getInstructor().getId());
        response.setCategoryId(c.getCategory().getId());
        response.setCreatedAt(c.getCreatedAt());
        return response;
    }

    //=================
    // Student related
    //=================

    // Browse all available courses
    public List<CourseDTO> browseAvailableCourses() {
        return courseRepository.findAll()
                .stream()
                .filter(Course::isPublished)
                .map(CourseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Search courses by name / category
    public List<CourseDTO> searchCourses(String keyword, Long categoryId) {
        if (keyword != null && !keyword.isBlank()) {
            List<Course> byTitle = courseRepository
                    .findByTitleContainingIgnoreCaseAndPublishedTrue(keyword);
            if (categoryId != null) {
                byTitle = byTitle.stream()
                        .filter(c -> c.getCategory().getId().equals(categoryId))
                        .collect(Collectors.toList());
            }
            return byTitle.stream().map(CourseDTO::fromEntity).collect(Collectors.toList());
        }
        if (categoryId != null) {
            return courseRepository.findByCategory_IdAndPublishedTrue(categoryId)
                    .stream().map(CourseDTO::fromEntity).collect(Collectors.toList());
        }
        return List.of();
    }

    // Watch video lectures
    public String watchLesson(Long courseId, Long lessonId) {
        User student = getLoggedInStudent();

        enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"))
                .getVideoUrl();
    }


    // View grades and progress
    public StudentProgressResponse getStudentProgress(Long courseId) {
        User student = getLoggedInStudent();

        enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<QuizScoreResponse> quizScores = quizAttemptRepository
                .findByStudentAndQuizLessonIdOrderBySubmittedAtDesc(student, courseId)
                .stream().map(QuizScoreResponse::fromEntity).collect(Collectors.toList());

        Double averageQuizScore = quizAttemptRepository
                .getAverageQuizScoreByStudentAndCourse(student.getId(), courseId);

        List<ExamScoreResponse> examAttempts = examAttemptRepository
                .findByStudentAndCourseIdOrderByAttemptNumberDesc(student, courseId)
                .stream().map(ExamScoreResponse::fromEntity).collect(Collectors.toList());

        Double overallProgress = courseProgressRepository
                .getCourseProgressAverage(student.getId(), courseId);

        return new StudentProgressResponse(
                course.getTitle(),
                overallProgress != null ? overallProgress : 0.0,
                averageQuizScore != null ? averageQuizScore : 0.0,
                quizScores,
                examAttempts
        );
    }

    // Private helper methods

    private  User getLoggedInStudent() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

}