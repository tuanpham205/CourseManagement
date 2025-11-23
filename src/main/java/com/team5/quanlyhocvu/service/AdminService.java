package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.*;
import com.team5.quanlyhocvu.repository.*;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import com.team5.quanlyhocvu.service.exception.DataConflictException;
import com.team5.quanlyhocvu.service.exception.RegistrationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final CourseRepository courseRepository;
    private final EnglishLevelRepository englishLevelRepository; // Đã thêm
    private final PasswordEncoder passwordEncoder;
    private final EnglishLevelService englishLevelService;

    public AdminService(AdminRepository adminRepository,
                        StudentRepository studentRepository,
                        TeacherRepository teacherRepository,
                        ClassroomRepository classroomRepository,
                        CourseRepository courseRepository,
                        PasswordEncoder passwordEncoder,
                        EnglishLevelService englishLevelService,
                        EnglishLevelRepository englishLevelRepository) { // Đã thêm vào constructor
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.classroomRepository = classroomRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
        this.englishLevelService = englishLevelService;
        this.englishLevelRepository = englishLevelRepository; // Khởi tạo
    }

    // =======================================
    // 1. QUẢN LÝ TÀI KHOẢN (CRUD ADMIN)
    // =======================================

    @Transactional
    public Admin saveAdmin(Admin admin) {
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new DataConflictException("Email " + admin.getEmail() + " đã tồn tại trong hệ thống.");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    public Optional<Admin> getAdminById(Integer id) {
        return adminRepository.findById(id);
    }

    /**
     * Tạo tài khoản Học viên mới. (ĐÃ SỬA LỖI KHỞI TẠO EnglishLevel)
     */
    @Transactional
    public Student createStudentAccount(Student student) {

        if(studentRepository.existsByEmail(student.getEmail())) {
            throw new DataConflictException("Email da ton tai!");
        }

        EnglishLevel newLevel = new EnglishLevel();
        newLevel = englishLevelRepository.save(newLevel);
        student.setLevel(newLevel);
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setRole("STUDENT");

        return studentRepository.save(student);
    }

    /**
     * Tạo tài khoản Giảng viên mới.
     */
    @Transactional
    public Teacher createTeacherAccount(Teacher teacher) {
        if(teacherRepository.existsByEmail(teacher.getEmail())) {
            throw new DataConflictException("Email da ton tai!");
        }
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        teacher.setRole("TEACHER");
        return teacherRepository.save(teacher);
    }

    // =======================================
    // 2. QUẢN LÝ LỚP HỌC VÀ PHÂN CÔNG
    // =======================================

    /**
     * Gán học viên vào lớp học.
     */
    @Transactional
    public Student assignStudentToClass(Integer studentId, Integer classId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Học viên ID: " + studentId));

        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Lớp học ID: " + classId));

        // KIỂM TRA 1: Học viên đã ở trong lớp này chưa
        Classroom currentClassroom = student.getCurrentClassroom();

        if (currentClassroom != null && classId.equals(currentClassroom.getId())) {
            throw new RegistrationException("Học viên ID " + studentId + " đã đăng ký lớp này rồi.");
        }

        // KIỂM TRA 2: Trình độ có phù hợp không
        EnglishLevel studentLevel = student.getLevel();

        if (studentLevel == null) {
            throw new RegistrationException("Học viên chưa có thông tin trình độ tiếng Anh.");
        }

        String requiredStandard = classroom.getInputStandard();
        String studentComparisonLevel = studentLevel.getComparisonLevel();

        if (studentComparisonLevel == null || studentComparisonLevel.isEmpty()) {
            throw new RegistrationException("Học viên chưa có điểm số được cập nhật để so sánh.");
        }

        boolean meetsStandard = englishLevelService.meetsInputStandard(
                studentComparisonLevel,
                requiredStandard
        );

        if (!meetsStandard) {
            throw new RegistrationException(String.format(
                    "Trình độ hiện tại (%s) của học viên KHÔNG ĐẠT tiêu chuẩn đầu vào (%s) của lớp học.",
                    studentComparisonLevel,
                    requiredStandard
            ));
        }

        // Gán lớp mới
        student.setCurrentClassroom(classroom);
        return studentRepository.save(student);
    }

    /**
     * Phân công Giáo viên vào Lớp học (Đảm bảo tính nhất quán dữ liệu 2 chiều)
     */
    @Transactional
    public Teacher assignTeacherToClassroom(Integer teacherId, Integer classroomId) {
        Teacher newTeacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Giảng viên ID: " + teacherId));

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Lớp học ID: " + classroomId));

        Integer oldTeacherId = classroom.getTeacherId();

        if (teacherId.equals(oldTeacherId)) {
            if (!newTeacher.getClassroomIds().contains(classroomId)) {
                newTeacher.addClassroomId(classroomId);
                return teacherRepository.save(newTeacher);
            }
            return newTeacher;
        }

        if (oldTeacherId != null) {
            teacherRepository.findById(oldTeacherId).ifPresent(oldTeacher -> {
                oldTeacher.removeClassroomId(classroomId);
                teacherRepository.save(oldTeacher);
            });
        }

        classroom.setTeacherId(teacherId);
        classroomRepository.save(classroom);

        newTeacher.addClassroomId(classroomId);
        return teacherRepository.save(newTeacher);
    }

    /**
     * Tạo Course mới.
     */
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    /**
     * Tạo Classroom mới và gán nó cho một Course.
     */
    public Classroom createClassroom(Integer courseId, Classroom classroom) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Course ID: " + courseId));

        classroom.setCourse(course);
        return classroomRepository.save(classroom);
    }

    public List<Course> getAllCourses() {
        return this.courseRepository.findAll();
    }

    public List<Classroom> getAllClassrooms() {
        return this.classroomRepository.findAll();
    }
}