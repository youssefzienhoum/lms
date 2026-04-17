package com.lms.lms.DTOS;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
@Data
public class CourseResponseDto {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private BigDecimal price;
    private Boolean Free;
    private Boolean Published;
    private Integer totalLessons;
    private Integer totalDuration;
    private Long instructorId;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
