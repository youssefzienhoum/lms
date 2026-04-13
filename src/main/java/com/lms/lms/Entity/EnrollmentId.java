// package com.lms.lms.Entity;

// import java.io.Serializable;
// import java.util.Objects;

// public class EnrollmentId implements Serializable {
    
//     private Long studentId;
//     private Long courseId;
    
//     // Default constructor
//     public EnrollmentId() {}
    
//     // Parameterized constructor
//     public EnrollmentId(Long studentId, Long courseId) {
//         this.studentId = studentId;
//         this.courseId = courseId;
//     }
    
//     // Getters & Setters
//     public Long getStudentId() { return studentId; }
//     public void setStudentId(Long studentId) { this.studentId = studentId; }
    
//     public Long getCourseId() { return courseId; }
//     public void setCourseId(Long courseId) { this.courseId = courseId; }
    
//     // ✅ EQUALS & HASHCODE (مهم جداً!)
//     @Override
//     public boolean equals(Object o) {
//         if (this == o) return true;
//         if (o == null || getClass() != o.getClass()) return false;
//         EnrollmentId that = (EnrollmentId) o;
//         return Objects.equals(studentId, that.studentId) &&
//                Objects.equals(courseId, that.courseId);
//     }
    
//     @Override
//     public int hashCode() {
//         return Objects.hash(studentId, courseId);
//     }
// }