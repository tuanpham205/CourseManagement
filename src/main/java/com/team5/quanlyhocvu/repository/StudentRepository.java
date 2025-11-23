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


    List<Student> findByEnglishLevel_VstepLevel(String vstepLevel);


    List<Student> findByEnglishLevel_IeltsBand(Double ieltsBand);


    List<Student> findByEnglishLevel_ToeicScore(Integer toeicScore);

    List<Student> findByCurrentClassroom_Id(Integer classroomId);

    boolean existsByEmail(String email);

    List<Student> findByCurrentClassroomIsNull();

    long countByCurrentClassroom_Id(Integer classroomId);

}