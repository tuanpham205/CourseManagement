package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {

    /**
     * Tìm kiếm Lớp học theo Tên (className).
     */
    Optional<Classroom> findByClassName(String className);

    /**
     * Tìm kiếm danh sách Lop hoc theo Trình độ đầu vào (inputStandard).
     */
    List<Classroom> findByInputStandard(String inputStandard);

    /**
     * Tìm kiếm danh sách tất cả các lớp mà một giáo viên đang dạy.
     * Sử dụng Integer để đồng bộ với Model và cho phép giá trị null.
     */
    List<Classroom> findByTeacherId(Integer teacherId);

    /**
     * Tìm kiếm danh sách các lớp học thuộc về một Khoá học nhất định.
     * Sử dụng Integer để đồng bộ với Model và cho phép giá trị null.
     */
    List<Classroom> findByCourseId(Integer courseId);

    /**
     * 💡 Bổ sung: Tìm kiếm các lớp chưa có giáo viên (TeacherId = null).
     */
    List<Classroom> findByTeacherIdIsNull();
}