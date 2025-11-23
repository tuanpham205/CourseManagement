package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    // Check trùng tên
    Optional<Course> findByCourseName(String courseName);

    // Search theo tên
    List<Course> findByCourseNameContainingIgnoreCase(String courseName);

    // Check trùng tên khi update (loại trừ ID hiện tại)
    boolean existsByCourseNameAndIdNot(String courseName, Integer id);

    // Check tồn tại theo tên (dùng cho service)
    boolean existsByCourseName(String courseName);
}
