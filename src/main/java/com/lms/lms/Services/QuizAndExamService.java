package com.lms.lms.Services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lms.lms.DTOS.AnswerOptionResponse;
import com.lms.lms.DTOS.AnswerRequestDto;
import com.lms.lms.DTOS.AnswerSubmission;
import com.lms.lms.DTOS.AttemptResultResponse;
import com.lms.lms.DTOS.ExamRequestDto;
import com.lms.lms.DTOS.ExamResponse;
import com.lms.lms.DTOS.ExamScoreResponse;
import com.lms.lms.DTOS.ExamSubmissionRequest;
import com.lms.lms.DTOS.QuestionRequestDto;
import com.lms.lms.DTOS.QuestionResponse;
import com.lms.lms.DTOS.QuizRequestDto;
import com.lms.lms.DTOS.QuizResponse;
import com.lms.lms.DTOS.QuizScoreResponse;
import com.lms.lms.DTOS.QuizSubmissionRequest;
import com.lms.lms.DTOS.StudentProgressResponse;
import com.lms.lms.Entity.Answer;
import com.lms.lms.Entity.Certificate;
import com.lms.lms.Entity.Course;
import com.lms.lms.Entity.CourseExam;
import com.lms.lms.Entity.CourseProgress;
import com.lms.lms.Entity.Enrollment;
import com.lms.lms.Entity.ExamAttempt;
import com.lms.lms.Entity.Lesson;
import com.lms.lms.Entity.Question;
import com.lms.lms.Entity.Quiz;
import com.lms.lms.Entity.QuizAttempt;
import com.lms.lms.Entity.User;
import com.lms.lms.Repo.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizAndExamService {
        private final EnrollmentRepository enrollmentRepository;
        private final CourseRepository courseRepository;
        private final QuizRepository quizRepository;
        private final QuizAttemptRepository quizAttemptRepository;
        private final CourseExamRepository courseExamRepository;
        private final ExamAttemptRepository examAttemptRepository;
        private final CourseProgressRepository courseProgressRepository;
        private final LessonRepository lessonRepository;
        private final CertificateRepository certificateRepository;
        private final AnswerRepository answerRepository;
        private final QuestionRepository questionRepository;
        private final UserRepository userRepository;

    public QuizResponse CreeateQuiz(QuizRequestDto dto) {
        User instructor = getLoggedInInstructor();

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException("You don't own this course");
        }

        Lesson lesson = lessonRepository.findById(dto.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (!lesson.getCourse().getId().equals(course.getId())) {
            throw new RuntimeException("This lesson does not belong to the specified course");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setTimeLimit(dto.getTimeLimit());
        quiz.setTotalQuestions(dto.getTotalQuestions());
        quiz.setPassingScore(dto.getPassingScore());
        quiz.setPublished(dto.getPublished());
        quiz.setLesson(lesson);
        quiz.getLesson().getCourse().setInstructor(instructor);

        Quiz saved = quizRepository.save(quiz);
        return QuizResponse.fromEntity(saved);

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

    public  void DeleteQuiz(Long quizId) {
        User instructor = getLoggedInInstructor();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getLesson().getCourse().getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException("You don't own this quiz");
        }

        quizRepository.delete(quiz);
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
        
    public  QuestionResponse addQuestionQuiz(Long quizId, QuestionRequestDto dto) {
        User instructor = getLoggedInInstructor();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getLesson().getCourse().getInstructor().getId().equals(instructor.getId())) {
                throw new RuntimeException("You don't own this quiz");
        }
        Question question = buildQuestion(dto);
        question.setQuiz(quiz);
        Question saved = questionRepository.save(question);
        return QuestionResponse.fromEntity(saved);
        }

        public void deleteQuestionFromQuiz(Long quizId, Long questionId) {
                User instructor = getLoggedInInstructor();

                Quiz quiz = quizRepository.findById(quizId)
                        .orElseThrow(() -> new RuntimeException("Quiz not found"));

                if (!quiz.getLesson().getCourse().getInstructor().getId().equals(instructor.getId())) {
                        throw new RuntimeException("You don't own this quiz");
                }

                Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new RuntimeException("Question not found"));

                if (question.getQuiz() == null || !question.getQuiz().getId().equals(quizId)) {
                        throw new RuntimeException("Question does not belong to this quiz");
                }
                questionRepository.delete(question);
        }

    // Take final exam (fetch)
    public ExamResponse CreateExam(ExamRequestDto dto) {
        User instructor = getLoggedInInstructor();
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (!course.getInstructor().getId().equals(instructor.getId())) {
                throw new RuntimeException("You don't own this course");
        }
        CourseExam exam = new CourseExam();
        exam.setTitle(dto.getTitle());
        exam.setTimeLimit(dto.getTimeLimit());
        exam.setTotalQuestions(dto.getTotalQuestions());
        exam.setPassingScore(dto.getPassingScore());
        exam.setCourse(course);
        exam.getCourse().setInstructor(instructor);
        CourseExam saved = courseExamRepository.save(exam);
        return ExamResponse.fromEntity(saved);
    }



    public void deleteExam(Long examId) {
        User instructor = getLoggedInInstructor();
        CourseExam exam = courseExamRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        if (!exam.getCourse().getInstructor().getId().equals(instructor.getId())) {
                throw new RuntimeException("You don't own this course");
        }
        courseExamRepository.delete(exam);
}

        public QuestionResponse addQuestionToExam(Long ExamId,QuestionRequestDto dto) {
        User instructor = getLoggedInInstructor();

        CourseExam exam = courseExamRepository.findById(ExamId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (!exam.getCourse().getInstructor().getId().equals(instructor.getId())) {
                throw new RuntimeException("You don't own this exam");
        }

        Question question = buildQuestion(dto);
        question.setCourseExam(exam);

        Question saved = questionRepository.save(question);
        return QuestionResponse.fromEntity(saved);
}
        
        public void deleteQuestionForExam(Long questionId,Long examId) {
        User instructor = getLoggedInInstructor();

        CourseExam exam = courseExamRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (!exam.getCourse().getInstructor().getId().equals(instructor.getId())) {
                throw new RuntimeException("You don't own this exam");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (question.getCourseExam() == null || !question.getCourseExam().getId().equals(examId)) {
                throw new RuntimeException("Question does not belong to this exam");
        }

        questionRepository.delete(question);

}

// public QuestionResponse updateQuestionForExam(Long questionId, QuestionRequestDto dto) {
//         User instructor = getLoggedInInstructor();

//         Question question = questionRepository.findById(questionId)
//                 .orElseThrow(() -> new RuntimeException("Question not found"));

//         if (!question.getCourseExam().getCourse().getInstructor().getId().equals(instructor.getId())) {
//                 throw new RuntimeException("You don't own this question");
//         }

//         question.setQuestionText(dto.getQuestionText());
//         question.setPoints(dto.getPoints());

//         Question saved = questionRepository.save(question);
//         return QuestionResponse.fromEntity(saved);
// }

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
                return calculateCurrentProgress(student.getId(), courseId);
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

        double overallProgress = calculateCurrentProgress(student.getId(), courseId);

        return new StudentProgressResponse(
                course.getTitle(),
                overallProgress,
                averageQuizScore != null ? averageQuizScore : 0.0,
                quizScores,
                examAttempts
        );
}

    // Private helper methods

    private double calculateCurrentProgress(Long studentId, Long courseId) {
    long completedCount = courseProgressRepository
            .findByEnrollmentStudentIdAndEnrollmentCourseId(studentId, courseId)
            .stream()
            .filter(CourseProgress::isCompleted)
            .count();

    int totalLessons = lessonRepository.findByCourseIdOrderByLessonOrder(courseId).size();

    return totalLessons > 0
            ? (completedCount * 100.0 / totalLessons)
            : 0.0;
}

    private  User getLoggedInStudent() {
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
        
        private User getLoggedInInstructor() {
    var auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("User not authenticated");
    }

    
    boolean isInstructor = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));

    if (!isInstructor) {
        throw new RuntimeException("Access denied: Not an instructor");
    }

    String email = auth.getName();

    return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Instructor not found"));
}

        // public AnswerOptionResponse addAnswerToQuestion(Long questionId, AnswerRequestDto dto) {
        //         User instructor = getLoggedInInstructor();

        //         Question question = questionRepository.findById(questionId)
        //                 .orElseThrow(() -> new RuntimeException("Question not found"));

        //         validateInstructorOwnsQuestion(instructor, question);

        //         Answer answer = new Answer();
        //         answer.setAnswerText(dto.answerText());
        //         answer.setCorrect(dto.correct());
        //         answer.setAnswerOrder(dto.answerOrder());
        //         answer.setQuestion(question);

        //         Answer saved = answerRepository.save(answer);
        //         return AnswerOptionResponse.fromEntity(saved);
        // }

        public AnswerOptionResponse updateAnswer(Long questionId, Long answerId, AnswerRequestDto dto) {
                User instructor = getLoggedInInstructor();

                Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new RuntimeException("Question not found"));

                validateInstructorOwnsQuestion(instructor, question);

                Answer answer = answerRepository.findById(answerId)
                        .orElseThrow(() -> new RuntimeException("Answer not found"));

                if (!answer.getQuestion().getId().equals(questionId)) {
                        throw new RuntimeException("Answer does not belong to this question");
                }

                answer.setAnswerText(dto.answerText());
                answer.setCorrect(dto.correct());
                answer.setAnswerOrder(dto.answerOrder());

                Answer saved = answerRepository.save(answer);
                return AnswerOptionResponse.fromEntity(saved);
        }

        public void deleteAnswer(Long questionId, Long answerId) {
                User instructor = getLoggedInInstructor();

                Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new RuntimeException("Question not found"));

                validateInstructorOwnsQuestion(instructor, question);

                Answer answer = answerRepository.findById(answerId)
                        .orElseThrow(() -> new RuntimeException("Answer not found"));

                if (!answer.getQuestion().getId().equals(questionId)) {
                        throw new RuntimeException("Answer does not belong to this question");
                }

                answerRepository.delete(answer);
        }

        public List<AnswerOptionResponse> getAnswersByQuestion(Long questionId) {
                User instructor = getLoggedInInstructor();

                Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new RuntimeException("Question not found"));

        validateInstructorOwnsQuestion(instructor, question);

        return answerRepository.findByQuestionIdOrderByAnswerOrder(questionId)
            .stream()
            .map(AnswerOptionResponse::fromEntity)
            .toList();
        }


        private void validateInstructorOwnsQuestion(User instructor, Question question) {
                // Question belongs to a quiz
                if (question.getQuiz() != null) {
                if (!question.getQuiz().getLesson().getCourse().getInstructor().getId()
                .equals(instructor.getId())) {
                        throw new RuntimeException("You don't own this question");
                }
                return;
        }
    // Question belongs to an exam
                if (question.getCourseExam() != null) {
                if (!question.getCourseExam().getCourse().getInstructor().getId()
                        .equals(instructor.getId())) {
                        throw new RuntimeException("You don't own this question");
                }
                return;
                }
                throw new RuntimeException("Question is not linked to any quiz or exam");
        }

private Question buildQuestion(QuestionRequestDto dto) {
    Question question = new Question();
    question.setQuestionText(dto.questionText());
    question.setQuestionType(Question.QuestionType.valueOf(dto.questionType()));
    question.setQuestionOrder(dto.questionOrder());
    question.setPoints(dto.points() != null ? dto.points() : 1);

    if (dto.answers() != null && !dto.answers().isEmpty()) {
        List<Answer> answers = dto.answers().stream().map(a -> {
            Answer answer = new Answer();
            answer.setAnswerText(a.answerText());
            answer.setCorrect(a.correct());
            answer.setAnswerOrder(a.answerOrder());
            answer.setQuestion(question);
            return answer;
        }).toList();
        question.setAnswers(answers);
    }

    return question;
}
}

