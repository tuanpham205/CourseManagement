package com.team5.quanlyhocvu.controller;

import java.util.List;
import com.team5.quanlyhocvu.model.*;
import com.team5.quanlyhocvu.service.AdminService;
import com.team5.quanlyhocvu.service.EnglishLevelService;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final EnglishLevelService englishLevelService;
    public AdminController(AdminService adminService, EnglishLevelService englishLevelService) {
        this.adminService = adminService;
        this.englishLevelService = englishLevelService;
    }
    // TẠO TÀI KHOẢN (Admin, Student, Teacher)

    // Tạo Admin mới
    @PostMapping("/users/admin")
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin savedAdmin = adminService.saveAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }

    // Lấy thông tin Admin theo ID
    @GetMapping("/users/admin/{id}")
    public ResponseEntity<Admin> getAdmin(@PathVariable Integer id) {
        Admin admin = adminService.getAdminById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay Admin co ID: " + id));
        return ResponseEntity.ok(admin);
    }

    // Tạo tài khoản Học viên mới
    @PostMapping("/users/student")
    public ResponseEntity<Student> createStudent(@RequestBody  Student student) {
        Student saved = adminService.createStudentAccount(student);

        saved.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Tạo tài khoản Giáo viên mới
    @PostMapping("/users/teacher")
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        Teacher saved = adminService.createTeacherAccount(teacher);
        saved.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Tạo khóa học mới
    @PostMapping("/course")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course saved = adminService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Lấy danh sách khóa học
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(adminService.getAllCourses());
    }

    // Tạo lớp học
    @PostMapping("/course/{courseId}/classroom")
    public ResponseEntity<Classroom> createClassroom(
            @PathVariable Integer courseId,
            @RequestBody Classroom classroom
    ) {
        Classroom saved = adminService.createClassroom(courseId, classroom);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Lấy danh sách lớp hoc
    @GetMapping("/classrooms")
    public ResponseEntity<List<Classroom>> getAllClassrooms() {
        return ResponseEntity.ok(adminService.getAllClassrooms());
    }


    // GÁN LỚP HỌC (ASSIGNMENTS)

    /**
     * API gán học viên vào lớp học.
     * Sử dụng @RequestParam để lấy ID từ query string: /assignments/student/class?studentId=1&classId=101
     */
    @PutMapping("/assignments/student/class")
    public ResponseEntity<Student> assignStudentToClass(
            @RequestParam Integer studentId,
            @RequestParam Integer classId
    ) {
        Student updatedStudent = adminService.assignStudentToClass(studentId, classId);
        return ResponseEntity.ok(updatedStudent);
    }

    /**
     * API phân công Giáo viên vào Lớp học.
     * Sử dụng @RequestParam để lấy ID từ query string: /assignments/teacher/class?teacherId=2&classroomId=101
     */
    @PutMapping("/assignments/teacher/class")
    public ResponseEntity<Teacher> assignTeacherToClass(
            @RequestParam Integer teacherId,
            @RequestParam Integer classroomId
    ) {
        Teacher updatedTeacher = adminService.assignTeacherToClassroom(teacherId, classroomId);
        return ResponseEntity.ok(updatedTeacher);
    }
    //updatelevel
    @PutMapping("/students/{studentId}/english-level")
    public ResponseEntity<EnglishLevel> updateStudentLevel(
            @PathVariable int studentId,
            @RequestParam(required = false) Double ieltsBand,
            @RequestParam(required = false) Integer toeicScore,
            @RequestParam(required = false) String vstepLevel
    ) {
        EnglishLevel updated = englishLevelService.updateLevel(studentId, ieltsBand, toeicScore, vstepLevel);
        return ResponseEntity.ok(updated);
    }


}