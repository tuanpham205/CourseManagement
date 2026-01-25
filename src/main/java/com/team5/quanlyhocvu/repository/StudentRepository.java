package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    Optional<Student> findByEmail(String email);

    List<Student> findByFullnameContainingIgnoreCase(String fullname);

    Optional<Student> findByUsername(String username);


    List<Student> findByEnglishLevel_VstepLevel(String vstepLevel);


    List<Student> findByEnglishLevel_IeltsBand(Double ieltsBand);


    List<Student> findByEnglishLevel_ToeicScore(Integer toeicScore);

    List<Student> findByCurrentClassroom_Id(Integer classroomId);

    boolean existsByEmail(String email);

    List<Student> findByCurrentClassroomIsNull();

    long countByCurrentClassroom_Id(Integer classroomId);

    // ================== BÁO CÁO ==================

    // Tổng học viên (toàn hệ thống)
    @Query("SELECT COUNT(s) FROM Student s")
    long countTotalStudents();

    // Số học viên nhập học trong khoảng thời gian
    @Query("""
        SELECT COUNT(s)
        FROM Student s
        WHERE s.enrollmentDate BETWEEN :from AND :to
    """)
    long countStudentsBetween(
            @org.springframework.data.repository.query.Param("from") java.time.LocalDate from,
            @org.springframework.data.repository.query.Param("to")   java.time.LocalDate to
    );

}