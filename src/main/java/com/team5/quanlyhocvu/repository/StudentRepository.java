package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByEmail(String email);

    List<Student> findByFullnameContainingIgnoreCase(String fullname);

    Optional<Student> findByUsername(String username);

    List<Student> findByLevel(String level);

    /**
     * Tìm tất cả học viên thuộc một lớp học cụ thể.
     */
    List<Student> findByCurrentClassroomId(Integer classroomId);

    /**
     * Lấy danh sách học viên hiện chưa được phân vào lớp nào (currentClassroomId = null).
     */
    List<Student> findByCurrentClassroomIdIsNull();

    /**
     * Phương thức đếm chuẩn hóa, được sử dụng trong ClassroomService.
     * Đếm tổng số học viên đang học trong một lớp cụ thể.
     */
    long countByCurrentClassroomId(Integer classroomId);

}