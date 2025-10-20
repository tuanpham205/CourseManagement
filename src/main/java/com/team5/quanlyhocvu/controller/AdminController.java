package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.model.Admin;
import com.team5.quanlyhocvu.model.Student;
import com.team5.quanlyhocvu.model.Teacher;
import com.team5.quanlyhocvu.service.AdminService;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Giả định các DTO sau tồn tại để truyền dữ liệu tạo/phân công một cách rõ ràng
// import com.team5.quanlyhocvu.dto.UserCreationRequest;
// import com.team5.quanlyhocvu.dto.TeacherAssignRequest;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Tạo Admin mới (Siêu Quản trị)
     * POST /api/admin/users/admin
     */
    @PostMapping("/users/admin")
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        // Lưu ý: Mật khẩu được hash trong AdminService
        Admin savedAdmin = adminService.saveAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }

    /**
     * 🔍 Lấy thông tin Admin theo ID
     * GET /api/admin/users/admin/{id}
     */
    @GetMapping("/users/admin/{id}")
    public ResponseEntity<Admin> getAdmin(@PathVariable Integer id) {
        Admin admin = adminService.getAdminById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay Admin co ID: " + id));
        return ResponseEntity.ok(admin);
    }

    /**
     * 👨‍🎓 Tạo tài khoản Học viên mới
     * POST /api/admin/users/student
     * Giả định RequestBody là Student model
     */
    @PostMapping("/users/student")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        // Logic hash mật khẩu và lưu Student
        Student newStudent = adminService.createStudentAccount(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(newStudent);
    }

    /**
     * 👨‍🏫 Tạo tài khoản Giáo viên mới
     * POST /api/admin/users/teacher
     * Giả định RequestBody là Teacher model
     */
    @PostMapping("/users/teacher")
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        // Logic hash mật khẩu và lưu Teacher
        Teacher newTeacher = adminService.createTeacherAccount(teacher);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTeacher);
    }
    /**
     * 🏷️ API gán học viên vào lớp học
     * PUT /api/admin/assignments/student/class
     */
    @PutMapping("/assignments/student/class")
    public ResponseEntity<Student> assignStudentToClass(
            @RequestParam Integer studentId,
            @RequestParam Integer classId
    ) {
        Student updatedStudent = adminService.assignStudentToClass(studentId, classId);
        return ResponseEntity.ok(updatedStudent); // Trả về đối tượng Student đã cập nhật
    }

    /**
     * 🏷️ API phân công Giáo viên vào Lớp học
     * PUT /api/admin/assignments/teacher/class
     * Body: { "teacherId": 101, "classroomId": 201 }
     */
    @PutMapping("/assignments/teacher/class")
    public ResponseEntity<Teacher> assignTeacherToClass(
            @RequestParam Integer teacherId,
            @RequestParam Integer classroomId
    ) {
        Teacher updatedTeacher = adminService.assignTeacherToClassroom(teacherId, classroomId);
        return ResponseEntity.ok(updatedTeacher); // Trả về đối tượng Teacher đã cập nhật
    }

}