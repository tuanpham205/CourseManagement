package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.*;
import com.team5.quanlyhocvu.repository.*;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import com.team5.quanlyhocvu.service.exception.DataConflictException;
import com.team5.quanlyhocvu.service.exception.RegistrationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final CourseRepository courseRepository;
    private final EnglishLevelRepository englishLevelRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnglishLevelService englishLevelService;
    private final UserRepository userRepository;

    public AdminService(AdminRepository adminRepository,
                        StudentRepository studentRepository,
                        TeacherRepository teacherRepository,
                        ClassroomRepository classroomRepository,
                        CourseRepository courseRepository,
                        PasswordEncoder passwordEncoder,
                        EnglishLevelService englishLevelService,
                        EnglishLevelRepository englishLevelRepository,
                        UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.classroomRepository = classroomRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
        this.englishLevelService = englishLevelService;
        this.englishLevelRepository = englishLevelRepository;
        this.userRepository = userRepository;
    }

    // =======================================
    // 1. QUẢN LÝ TÀI KHOẢN
    // =======================================

    @Transactional
    public Admin saveAdmin(Admin admin, String rawPassword) {
        if (userRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new DataConflictException("Email " + admin.getEmail() + " đã tồn tại.");
        }

        User user = new User();
        user.setEmail(admin.getEmail());
        user.setFullName(admin.getFullName());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("ADMIN");
        user.setEnabled(true);
        userRepository.save(user);

        return adminRepository.save(admin);
    }

    public Optional<Admin> getAdminById(Integer id) {
        return adminRepository.findById(id);
    }

    @Transactional
    public Student createStudentAccount(Student student, String rawPassword) {
        if (userRepository.existsByEmail(student.getEmail())) {
            throw new DataConflictException("Email đã tồn tại.");
        }

        User user = new User();
        user.setEmail(student.getEmail());
        user.setFullName(student.getFullname());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("STUDENT");
        user.setEnabled(true);
        userRepository.save(user);

        // Khởi tạo trình độ và gán ngày nhập học mặc định (Ngày hôm nay)
        EnglishLevel newLevel = englishLevelRepository.save(new EnglishLevel());
        student.setLevel(newLevel);
        student.setRole("STUDENT");

        if (student.getEnrollmentDate() == null) {
            student.setEnrollmentDate(LocalDate.now());
        }

        return studentRepository.save(student);
    }

    @Transactional
    public Teacher createTeacherAccount(Teacher teacher, String rawPassword) {
        if (userRepository.existsByEmail(teacher.getEmail())) {
            throw new DataConflictException("Email đã tồn tại.");
        }

        User user = new User();
        user.setEmail(teacher.getEmail());
        user.setFullName(teacher.getFullname());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("TEACHER");
        user.setEnabled(true);
        userRepository.save(user);

        teacher.setRole("TEACHER");
        return teacherRepository.save(teacher);
    }

    // =======================================
    // 2. QUẢN LÝ LỚP HỌC VÀ PHÂN CÔNG
    // =======================================

    @Transactional
    public Student assignStudentToClass(Integer studentId, Integer classId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Học viên ID: " + studentId));

        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Lớp học ID: " + classId));

        if (student.getCurrentClassroom() != null && classId.equals(student.getCurrentClassroom().getId())) {
            throw new RegistrationException("Học viên đã ở trong lớp này.");
        }

        // Logic kiểm tra trình độ đầu vào
        if (student.getLevel() == null || student.getLevel().getComparisonLevel() == null) {
            throw new RegistrationException("Học viên chưa có dữ liệu trình độ để xét lớp.");
        }

        boolean meetsStandard = englishLevelService.meetsInputStandard(
                student.getLevel().getComparisonLevel(),
                classroom.getInputStandard()
        );

        if (!meetsStandard) {
            throw new RegistrationException("Trình độ học viên không đạt tiêu chuẩn đầu vào của lớp.");
        }

        student.setCurrentClassroom(classroom);
        return studentRepository.save(student);
    }

    @Transactional
    public Teacher assignTeacherToClassroom(Integer teacherId, Integer classroomId) {
        Teacher newTeacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Giảng viên ID: " + teacherId));

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Lớp học ID: " + classroomId));

        Integer oldTeacherId = classroom.getTeacherId();

        // Nếu giảng viên mới trùng giảng viên cũ, chỉ cập nhật danh sách lớp nếu thiếu
        if (teacherId.equals(oldTeacherId)) {
            if (!newTeacher.getClassroomIds().contains(classroomId)) {
                newTeacher.addClassroomId(classroomId);
                return teacherRepository.save(newTeacher);
            }
            return newTeacher;
        }

        // Xóa lớp khỏi danh sách của giảng viên cũ (nếu có)
        if (oldTeacherId != null) {
            teacherRepository.findById(oldTeacherId).ifPresent(oldTeacher -> {
                oldTeacher.removeClassroomId(classroomId);
                teacherRepository.save(oldTeacher);
            });
        }

        // Cập nhật giảng viên mới cho lớp và ngược lại
        classroom.setTeacherId(teacherId);
        classroomRepository.save(classroom);

        newTeacher.addClassroomId(classroomId);
        return teacherRepository.save(newTeacher);
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public Classroom createClassroom(Integer courseId, Classroom classroom) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Course ID: " + courseId));

        // Kiểm tra logic thời gian vận hành lớp học
        if (classroom.getStartDate() != null && classroom.getEndDate() != null) {
            if (classroom.getEndDate().isBefore(classroom.getStartDate())) {
                throw new IllegalArgumentException("Ngày kết thúc học tập phải sau ngày khai giảng.");
            }
        }

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