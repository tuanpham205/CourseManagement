package com.team5.quanlyhocvu.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.team5.quanlyhocvu.model.*;
import com.team5.quanlyhocvu.service.AdminService;
import com.team5.quanlyhocvu.service.EnglishLevelService;
import com.team5.quanlyhocvu.service.RegistrationRequestService;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.team5.quanlyhocvu.service.InvoiceService;
import com.team5.quanlyhocvu.service.PaymentService;
import com.team5.quanlyhocvu.service.StudentService;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final EnglishLevelService englishLevelService;
    private final RegistrationRequestService registrationRequestService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final StudentService studentService;

    public AdminController(AdminService adminService, EnglishLevelService englishLevelService, RegistrationRequestService registrationRequestService, InvoiceService invoiceService, PaymentService paymentService, StudentService studentService) {
        this.adminService = adminService;
        this.englishLevelService = englishLevelService;
        this.registrationRequestService = registrationRequestService;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
        this.studentService = studentService;
    }

    // TẠO TÀI KHOẢN (Admin, Teacher)

    // Tạo Admin mới
    @PostMapping("/users/admin")
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, Object> data) {
        // 1. Lấy thông tin admin
        Admin admin = new Admin();
        admin.setFullName((String) data.get("fullName"));
        admin.setEmail((String) data.get("email"));

        // 2. Lấy mật khẩu
        String rawPassword = (String) data.get("password");

        if (rawPassword == null || admin.getEmail() == null) {
            return ResponseEntity.badRequest().body("Thiếu email hoặc mật khẩu");
        }

        Admin savedAdmin = adminService.saveAdmin(admin, rawPassword);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }

    // Lấy thông tin Admin theo ID
    @GetMapping("/users/admin/{id}")
    public ResponseEntity<Admin> getAdmin(@PathVariable Integer id) {
        Admin admin = adminService.getAdminById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay Admin co ID: " + id));
        return ResponseEntity.ok(admin);
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

    // updatelevel
    @PutMapping("/students/{studentId}/english-level")
    public ResponseEntity<EnglishLevel> updateStudentLevel(
            @PathVariable int studentId,
            @RequestBody Map<String, Object> levelData
    ) {
        Double ieltsBand = (Double) levelData.get("ieltsBand");
        Integer toeicScore = (Integer) levelData.get("toeicScore");
        String vstepLevel = (String) levelData.get("vstepLevel");
        EnglishLevel updated = englishLevelService.updateLevel(
                studentId,
                ieltsBand,
                toeicScore,
                vstepLevel
        );
        return ResponseEntity.ok(updated);
    }

    /**
     * API Lấy danh sách yêu cầu tư vấn (Lead Management).
     */
    @GetMapping("/consultation/requests")
    public ResponseEntity<List<RegistrationRequest>> getAllRegistrationRequests() {
        return ResponseEntity.ok(registrationRequestService.getAllRequests());
    }

    /**
     * API Cập nhật trạng thái, ghi chú và BỔ SUNG LEVEL bởi Admin.
     */
    @PutMapping("/consultation/requests/{id}")
    public ResponseEntity<RegistrationRequest> updateRegistrationRequest(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> updateFields
    ) {
        RegistrationRequest updatedRequest = registrationRequestService.updateRequest(id, updateFields);
        return ResponseEntity.ok(updatedRequest);
    }


    // ----------------------------------------------------------------------
    // QUẢN LÝ THANH TOÁN (INVOICE & PAYMENT)
    // ----------------------------------------------------------------------

    /**
     * API Tạo Hóa đơn mới cho một Lead (Registration Request)
     */
    @PostMapping("/invoices")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Map<String, Object> invoiceData) {
        Integer registrationRequestId = (Integer) invoiceData.get("registrationRequestId");
        Integer courseId = (Integer) invoiceData.get("courseId");
        BigDecimal totalAmount = new BigDecimal(invoiceData.get("totalAmount").toString());
        LocalDate dueDate = LocalDate.parse((String) invoiceData.get("dueDate"));

        Invoice savedInvoice = invoiceService.createInvoice(
                registrationRequestId,
                courseId,
                totalAmount,
                dueDate
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(savedInvoice);
    }

    /**
     * API Ghi nhận Giao dịch Thanh toán (Admin xác nhận đã nhận tiền)
     */
    @PostMapping("/payments/record")
    public ResponseEntity<Payment> recordPayment(@RequestBody Map<String, Object> paymentData) {
        Long invoiceId = Long.valueOf(paymentData.get("invoiceId").toString());
        BigDecimal amount = new BigDecimal(paymentData.get("amount").toString());
        String methodStr = (String) paymentData.get("methodStr");
        String note = (String) paymentData.get("note");

        Payment recordedPayment = paymentService.recordPayment(
                invoiceId,
                amount,
                methodStr,
                note
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(recordedPayment);
    }

    /**
     * API Xem chi tiết Hóa đơn theo ID
     */
    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoiceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay Hoa don co ID: " + id));
        return ResponseEntity.ok(invoice);
    }


    // ----------------------------------------------------------------------
    // BẢO VỆ NGHIỆP VỤ TẠO STUDENT (GHI DANH)
    // ----------------------------------------------------------------------

    /**
     * API CHÍNH THỨC Tạo tài khoản Học viên từ Lead (sau khi thanh toán).
     * Hàm này kiểm tra InvoiceStatus = PAID trước khi tạo Student.
     */
    @PostMapping("/students/enroll/{registrationRequestId}")
    public ResponseEntity<Student> enrollStudent(
            @PathVariable Integer registrationRequestId
    ) {
        Student saved = studentService.createStudentAccount(registrationRequestId);

        // Xóa mật khẩu trước khi trả về để bảo mật
        saved.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}