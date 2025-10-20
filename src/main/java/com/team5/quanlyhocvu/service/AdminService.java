package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.Admin;
import com.team5.quanlyhocvu.model.Student;
import com.team5.quanlyhocvu.model.Teacher;
import com.team5.quanlyhocvu.model.Classroom;
import com.team5.quanlyhocvu.repository.AdminRepository;
import com.team5.quanlyhocvu.repository.StudentRepository;
import com.team5.quanlyhocvu.repository.TeacherRepository;
import com.team5.quanlyhocvu.repository.ClassroomRepository;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import com.team5.quanlyhocvu.service.exception.DataConflictException;
import com.team5.quanlyhocvu.service.exception.RegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository; // Thêm TeacherRepository
    private final ClassroomRepository classroomRepository;

    // Giả định: private final PasswordEncoder passwordEncoder; // Cần dùng để hash mật khẩu

    public AdminService(AdminRepository adminRepository,
                        StudentRepository studentRepository,
                        TeacherRepository teacherRepository,
                        ClassroomRepository classroomRepository) {
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.classroomRepository = classroomRepository;
    }

    // =======================================
    // 1. QUẢN LÝ TÀI KHOẢN (CRUD ADMIN)
    // =======================================

    @Transactional
    public Admin saveAdmin(Admin admin) {
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new DataConflictException("Email " + admin.getEmail() + " đã tồn tại trong hệ thống.");
        }
        // Admin.setPassword(passwordEncoder.encode(admin.getPassword())); // PHẢI HASH MẬT KHẨU
        return adminRepository.save(admin);
    }

    public Optional<Admin> getAdminById(Integer id) {
        return adminRepository.findById(id);
    }


    @Transactional
    public Student createStudentAccount(Student student) {
        // Kiểm tra email và username trùng lặp
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new DataConflictException("Email " + student.getEmail() + " đã tồn tại.");
        }
        // Student.setPassword(passwordEncoder.encode(student.getPassword())); // PHẢI HASH MẬT KHẨU
        return studentRepository.save(student);
    }

    @Transactional
    public Teacher createTeacherAccount(Teacher teacher) {
        if (teacherRepository.findByEmail(teacher.getEmail()).isPresent()) {
            throw new DataConflictException("Email " + teacher.getEmail() + " đã tồn tại.");
        }
        // Teacher.setPassword(passwordEncoder.encode(teacher.getPassword())); // PHẢI HASH MẬT KHẨU
        return teacherRepository.save(teacher);
    }


    /**
     * Gán học viên vào lớp học (đã sửa logic theo model)
     * @param studentId ID học viên
     * @param classId ID lớp học
     */
    @Transactional
    public Student assignStudentToClass(Integer studentId, Integer classId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Học viên ID: " + studentId));

        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Lớp học ID: " + classId));

        // KIỂM TRA: Học viên đã ở trong lớp này chưa
        if (student.getCurrentClassroomId() != null && student.getCurrentClassroomId().equals(classId)) {
            throw new RegistrationException("Học viên ID " + studentId + " đã đăng ký lớp này rồi.");
        }

        if (!student.getLevel().equals(classroom.getInputStandard())) {
            throw new RegistrationException("Trình độ đầu vào (" + classroom.getInputStandard()
                    + ") của lớp không phù hợp với trình độ hiện tại (" + student.getLevel()
                    + ") của học viên.");
        }
        student.setCurrentClassroomId(classId);
        return studentRepository.save(student);
    }

    /**
     * Phân công Giáo viên vào Lớp học
     */
    @Transactional
    public Teacher assignTeacherToClassroom(Integer teacherId, Integer classroomId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Giảng viên ID: " + teacherId));

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Lớp học ID: " + classroomId));

        Integer oldTeacherId = classroom.getTeacherId();
        // Xử lý giáo viên cũ (nếu có và không phải là giáo viên mới)
        if (oldTeacherId != null && !oldTeacherId.equals(teacherId)) {
            teacherRepository.findById(oldTeacherId).ifPresent(oldTeacher -> {
                oldTeacher.removeClassroomId(classroomId);
                teacherRepository.save(oldTeacher);
            });
        }

        // Gán giáo viên mới vào lớp
        classroom.setTeacherId(teacherId);
        classroomRepository.save(classroom);

        // 2. Cập nhật Teacher (Giáo viên giữ danh sách lớp ID)
        teacher.addClassroomId(classroomId);
        return teacherRepository.save(teacher);
    }
}