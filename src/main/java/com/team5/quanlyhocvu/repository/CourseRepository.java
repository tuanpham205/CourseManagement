package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer>
{
    Optional<Course> findByCourseName(String courseName);
    // Tìm kiếm khóa học theo tên chứa chuỗi (dùng cho API tìm kiếm)
    List<Course> findByCourseNameContainingIgnoreCase(String courseName);
}