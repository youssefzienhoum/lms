package com.lms.lms.Services;

import com.lms.lms.DTOS.AnswerSubmission;
import com.lms.lms.DTOS.AttemptResultResponse;
import com.lms.lms.DTOS.CourseDTO;
import com.lms.lms.DTOS.CourseRequestDto;
import com.lms.lms.DTOS.CourseResponseDto;
import com.lms.lms.DTOS.EnrollmentResponse;
import com.lms.lms.DTOS.ExamResponse;
import com.lms.lms.DTOS.ExamScoreResponse;
import com.lms.lms.DTOS.ExamSubmissionRequest;
import com.lms.lms.DTOS.QuizResponse;
import com.lms.lms.DTOS.QuizScoreResponse;
import com.lms.lms.DTOS.QuizSubmissionRequest;
import com.lms.lms.DTOS.StudentProgressResponse;
import com.lms.lms.Entity.Category;
import com.lms.lms.Entity.Certificate;
import com.lms.lms.Entity.Course;
import com.lms.lms.Entity.CourseExam;
import com.lms.lms.Entity.CourseProgress;
import com.lms.lms.Entity.Enrollment;
import com.lms.lms.Entity.ExamAttempt;
import com.lms.lms.Entity.Quiz;
import com.lms.lms.Entity.QuizAttempt;
import com.lms.lms.Entity.User;
import com.lms.lms.Repo.AnswerRepository;
import com.lms.lms.Repo.CategoryRepository;
import com.lms.lms.Repo.CertificateRepository;
import com.lms.lms.Repo.CourseExamRepository;
import com.lms.lms.Repo.CourseProgressRepository;
import com.lms.lms.Repo.CourseRepository;
import com.lms.lms.Repo.EnrollmentRepository;
import com.lms.lms.Repo.ExamAttemptRepository;
import com.lms.lms.Repo.LessonRepository;
import com.lms.lms.Repo.QuizAttemptRepository;
import com.lms.lms.Repo.QuizRepository;
import com.lms.lms.Repo.UserRepository;
import com.lms.lms.ServiceAbstraction.ICourseService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final CourseExamRepository courseExamRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final AnswerRepository answerRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final CertificateRepository certificateRepository;

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
        List<Course> courses = courseRepository.findByInstructorid(instructorId);

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

    // Enroll in a course
    public EnrollmentResponse enrollInCourse(Long courseId) {
        User student = getLoggedInStudent();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.isPublished()) {
            throw new RuntimeException("Course is not available for enrollment");
        }

        Optional<Enrollment> existing = enrollmentRepository
                .findByStudentIdAndCourseId(student.getId(), courseId);
        if (existing.isPresent()) {
            return EnrollmentResponse.fromEntity(existing.get());
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(Enrollment.Status.ENROLLED);

        return EnrollmentResponse.fromEntity(enrollmentRepository.save(enrollment));
    }

    // View enrolled courses
    public List<EnrollmentResponse> getEnrolledCourses() {
        User student = getLoggedInStudent();

        return enrollmentRepository.findByStudent(student)
                .stream().map(EnrollmentResponse::fromEntity).collect(Collectors.toList());
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

    // Take lesson quiz (fetch)
    public QuizResponse getLessonQuiz(Long courseId, Long lessonId) {
        User student = getLoggedInStudent();

        enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        Quiz quiz = quizRepository.findByLessonIdAndPublishedTrue(lessonId)
                .orElseThrow(() -> new RuntimeException("No quiz found for this lesson"));

        List<QuizAttempt> attempts = quizAttemptRepository
                .findByStudentAndQuizLessonIdOrderBySubmittedAtDesc(student, lessonId);

        boolean alreadyPassed = attempts.stream()
                .anyMatch(a -> a.getStatus() == QuizAttempt.Status.PASSED);

        if (alreadyPassed) {
            throw new RuntimeException("You have already passed this quiz");
        }

        return QuizResponse.fromEntity(quiz);
    }

    // Take lesson quiz (submit)
    public AttemptResultResponse submitLessonQuiz(Long courseId, QuizSubmissionRequest request) {
        User student = getLoggedInStudent();

        enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        Quiz quiz = quizRepository.findById(request.quizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        int totalQuestions = request.answers().size();
        int correctAnswers = gradeAnswers(request.answers());
        BigDecimal score = calculateScore(correctAnswers, totalQuestions);
        boolean passed = score.compareTo(quiz.getPassingScore()) >= 0;

        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(student);
        attempt.setQuiz(quiz);
        attempt.setScore(score);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setStatus(passed ? QuizAttempt.Status.PASSED : QuizAttempt.Status.FAILED);

        QuizAttempt saved = quizAttemptRepository.save(attempt);
        return new AttemptResultResponse(saved.getId(), "QUIZ", score,
                quiz.getPassingScore(), passed, totalQuestions, correctAnswers);
    }

    // Take final exam (fetch)
    public ExamResponse getCourseExam(Long courseId) {
        User student = getLoggedInStudent();

        enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        CourseExam exam = courseExamRepository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("No exam found for this course"));

        List<ExamAttempt> previousAttempts = examAttemptRepository
                .findByStudentAndCourseIdOrderByAttemptNumberDesc(student, courseId);

        if (previousAttempts.size() >= exam.getMaxAttempts()) {
            throw new RuntimeException("You have reached the maximum number of attempts for this exam");
        }

        return ExamResponse.fromEntity(exam);
    }

    // Take final exam  (submit)
    public AttemptResultResponse submitCourseExam(ExamSubmissionRequest request) {
        User student = getLoggedInStudent();

        enrollmentRepository.findByStudentIdAndCourseId(student.getId(), request.courseId())
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        CourseExam exam = courseExamRepository.findByCourseId(request.courseId())
                .orElseThrow(() -> new RuntimeException("Exam not found for this course"));

        List<ExamAttempt> previousAttempts = examAttemptRepository
                .findByStudentAndCourseIdOrderByAttemptNumberDesc(student, request.courseId());

        if (previousAttempts.size() >= exam.getMaxAttempts()) {
            throw new RuntimeException("You have reached the maximum number of attempts for this exam");
        }

        int totalQuestions = request.answers().size();
        int correctAnswers = gradeAnswers(request.answers());
        BigDecimal score = calculateScore(correctAnswers, totalQuestions);
        boolean passed = score.compareTo(exam.getPassingScore()) >= 0;

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        ExamAttempt attempt = new ExamAttempt();
        attempt.setStudent(student);
        attempt.setCourseExam(exam);
        attempt.setCourse(course);
        attempt.setScore(score);
        attempt.setPassed(passed);
        attempt.setAttemptNumber(previousAttempts.size() + 1);

        ExamAttempt saved = examAttemptRepository.save(attempt);

        if (passed) {
            Certificate certificate = new Certificate();
            certificate.setStudent(student);
            certificate.setCourse(course);
            certificate.setFinalScore(score);
            certificate.setCertificateCode(UUID.randomUUID().toString().toUpperCase());
            certificateRepository.save(certificate);
        }

        return new AttemptResultResponse(saved.getId(), "EXAM", score,
                exam.getPassingScore(), passed, totalQuestions, correctAnswers);
    }

    // Mark lesson as completed (helper method for the calculation of the course progress)
    public double markLessonAsCompleted(Long courseId, Long lessonId) {
        User student = getLoggedInStudent();

        // Check enrollment and get it
        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        // Check lesson exists
        lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // If completed, return current progress
        Optional<CourseProgress> existing = courseProgressRepository
                .findByEnrollmentStudentIdAndEnrollmentCourseIdAndLessonId(
                        student.getId(), courseId, lessonId);

        if (existing.isPresent() && existing.get().isCompleted()) {
            Double currentProgress = courseProgressRepository
                    .getCourseProgressAverage(student.getId(), courseId);
            return currentProgress != null ? currentProgress : 0.0;
        }

        // Mark the lesson as completed
        CourseProgress progress = existing.orElse(new CourseProgress());
        progress.setEnrollment(enrollment);
        progress.setLesson(lessonRepository.findById(lessonId).get());
        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());

        // Calculate new percentage
        // how many lessons are completed
        long completedCount = courseProgressRepository
                .findByEnrollmentStudentIdAndEnrollmentCourseId(student.getId(), courseId)
                .stream()
                .filter(CourseProgress::isCompleted)
                .count() + (existing.isPresent() ? 0 : 1);

        int totalLessons = lessonRepository.findByCourseIdOrderByLessonOrder(courseId).size();

        double percentage = totalLessons > 0
                ? (completedCount * 100.0 / totalLessons)
                : 0.0;

        progress.setProgressPercentage(BigDecimal.valueOf(percentage)
                .setScale(2, RoundingMode.HALF_UP));

        courseProgressRepository.save(progress);

        return percentage;
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

    private User getLoggedInStudent() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    private int gradeAnswers(List<AnswerSubmission> answers) {
        int correct = 0;
        for (AnswerSubmission submission : answers) {
            boolean isCorrect = answerRepository
                    .findByQuestionIdAndCorrectTrue(submission.questionId())
                    .stream()
                    .anyMatch(a -> a.getId().equals(submission.selectedAnswerId()));
            if (isCorrect) correct++;
        }
        return correct;
    }

    private BigDecimal calculateScore(int correctAnswers, int totalQuestions) {
        if (totalQuestions == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(correctAnswers * 100.0 / totalQuestions)
                .setScale(2, RoundingMode.HALF_UP);
    }
}