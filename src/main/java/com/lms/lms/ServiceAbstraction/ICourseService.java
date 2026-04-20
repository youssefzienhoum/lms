package com.lms.lms.ServiceAbstraction;
import com.lms.lms.DTOS.CourseRequestDto;
import com.lms.lms.DTOS.CourseResponseDto;


import java.util.List;

public interface ICourseService {
    
    CourseResponseDto createCourse(CourseRequestDto dto, Long instructorId);
    CourseResponseDto updateCourse(Long courseId, CourseRequestDto dto, Long instructorId);
    void deleteCourse(Long courseId, Long instructorId);
    List<CourseResponseDto> getInstructorCourses(Long instructorId);
    CourseResponseDto getCourseById(Long courseId);
}
