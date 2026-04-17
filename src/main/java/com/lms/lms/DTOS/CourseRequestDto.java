package com.lms.lms.DTOS;

import java.math.BigDecimal;

import lombok.Data;
@Data
public class CourseRequestDto {
    private String title;
    private String description;
    private String thumbnailUrl;
    private BigDecimal price;
    private Boolean Free;
    private Integer totalLessons;
    private Integer totalDuration;
    private Long categoryId;
}
