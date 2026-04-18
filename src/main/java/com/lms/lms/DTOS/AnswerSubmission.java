package com.lms.lms.DTOS;

import java.util.List;

// Represents one answer the student picks for one question
public record AnswerSubmission(
        Long questionId,
        Long selectedAnswerId
) {}
