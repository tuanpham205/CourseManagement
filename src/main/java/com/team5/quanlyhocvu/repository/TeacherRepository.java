package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    /*
    Tim kiem giao vien theo Email, tra ve Optional.
    Dung cho kiem tra trung lap khi tao/cap nhat giao vien.
    */
    Optional<Teacher> findByEmail(String email);

    /*
     Tim kiem danh sach giao vien theo chuyen mon (Specialization).
     Tra ve List<Teacher> (List rong neu khong tim thay).
     */
    List<Teacher> findBySpecialization(String specialization);
    boolean existsByEmail(String email);

    /*
    Tim kiem danh sach giao vien theo ho ten.
     */
    List<Teacher> findByFullnameContaining(String fullname);
}