package com.lms.lms.DTOS;

import com.lms.lms.Entity.Lesson;

public record LessonResponse(
        Long id,
        String title,
        String description,
        String videoUrl,
        Integer duration,
        Integer lessonOrder,
        boolean preview
) {
    public static LessonResponse fromEntity(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getVideoUrl(),
                lesson.getDuration(),
                lesson.getLessonOrder(),
                lesson.isPreview()
        );
    }
}