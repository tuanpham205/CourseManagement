package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.Student;
import com.team5.quanlyhocvu.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /*
      Lấy danh sách tất cả học viên
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /*
      Lấy thông tin học viên theo ID
     */
    public Optional<Student> getStudentById(Integer id) {
        return studentRepository.findById(id);
    }

    /*
      Tìm học viên theo email
     */
    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    /*
      Lấy danh sách học viên theo level
     */
    public List<Student> getStudentsByVstepLevel(String vstepLevel) {
        return studentRepository.findByEnglishLevel_VstepLevel(vstepLevel);
    }

    /*
    Lấy danh sách học viên theo điểm IELTS
    */
    public List<Student> getStudentsByIeltsBand(Double ieltsBand) {
        return studentRepository.findByEnglishLevel_IeltsBand(ieltsBand);
    }

    /*
    Lấy danh sách học viên theo điểm TOEIC
    */
    public List<Student> getStudentsByToeicScore(Integer toeicScore) {
        return studentRepository.findByEnglishLevel_ToeicScore(toeicScore);
    }

    /*
      Tạo mới hoặc lưu học viên
     */
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    /*
      Cập nhật thông tin cá nhân của học viên (Update Profile)
     */
    public Student updateStudentProfile(Integer id, Student newStudentData) {
        return studentRepository.findById(id)
                .map(student -> {
                    student.setFullname(newStudentData.getFullname());
                    student.setEmail(newStudentData.getEmail());
                    student.setPhone(newStudentData.getPhone());
                    student.setDateOfBirth(newStudentData.getDateOfBirth());
                    student.setAddress(newStudentData.getAddress());
                    return studentRepository.save(student);
                })
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học viên có ID: " + id));
    }

    /*
      Xoá học viên theo ID
     */
    public void deleteStudent(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy học viên có ID: " + id);
        }
        studentRepository.deleteById(id);
    }
}
