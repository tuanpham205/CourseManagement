package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.Student;
import com.team5.quanlyhocvu.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Lấy danh sách tất cả học viên
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Lấy thông tin học viên theo ID
     */
    public Optional<Student> getStudentById(Integer id) {
        return studentRepository.findById(id);
    }

    /**
     * Tìm học viên theo email
     */
    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    /**
     * Lấy danh sách học viên theo level (VD: A2, B1, ...)
     */
    public List<Student> getStudentsByLevel(String level) {
        return studentRepository.findByLevel(level);
    }

    // Lưu ý: createStudent được giữ lại ở đây cho CRUD cơ bản, nhưng
    // trong logic ứng dụng, chỉ ADMINService mới gọi nó để tạo User sau tư vấn.
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    /**
     * 📝 Cập nhật thông tin cá nhân của học viên (Update Profile)
     * Đã loại bỏ logic 'paid'
     */
    public Student updateStudentProfile(Integer id, Student newStudentData) {
        return studentRepository.findById(id)
                .map(student -> {
                    // Cập nhật các thuộc tính kế thừa từ Person
                    student.setFullname(newStudentData.getFullname());
                    student.setEmail(newStudentData.getEmail());
                    student.setPhone(newStudentData.getPhone()); // Bổ sung
                    student.setDateOfBirth(newStudentData.getDateOfBirth());

                    // Cập nhật thuộc tính riêng của Student
                    student.setLevel(newStudentData.getLevel());
                    student.setAddress(newStudentData.getAddress()); // Bổ sung

                    // KHÔNG CẬP NHẬT currentClassroomId TẠI ĐÂY (AdminService sẽ làm)
                    // KHÔNG CẬP NHẬT paid (PaymentService sẽ làm)

                    return studentRepository.save(student);
                })
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học viên có ID: " + id));
    }

    /**
     * Xoá học viên theo ID (Delete)
     * Lưu ý: Nếu có Payment liên quan, cần xử lý quan hệ (cascade delete hoặc ràng buộc).
     */
    public void deleteStudent(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy học viên có ID: " + id);
        }
        studentRepository.deleteById(id);
    }
}