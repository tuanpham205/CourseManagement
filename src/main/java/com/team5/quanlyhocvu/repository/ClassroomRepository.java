package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {
    Optional<Classroom> findByClassName(String className);

    List<Classroom> findByInputStandard(String inputStandard);

    List<Classroom> findByTeacherId(Integer teacherId);

    List<Classroom> findByCourse_Id(Integer courseId);

    List<Classroom> findByTeacherIdIsNull();
}