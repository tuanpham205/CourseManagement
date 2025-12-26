package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.EnglishLevel;
import com.team5.quanlyhocvu.model.Student;
import com.team5.quanlyhocvu.model.RegistrationRequest;
import com.team5.quanlyhocvu.model.User;
import com.team5.quanlyhocvu.repository.EnglishLevelRepository;
import com.team5.quanlyhocvu.repository.StudentRepository;
import com.team5.quanlyhocvu.repository.RegistrationRequestRepository;
import com.team5.quanlyhocvu.repository.UserRepository;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final RegistrationRequestRepository registrationRequestRepository;
    private final InvoiceService invoiceService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnglishLevelRepository englishLevelRepository;

    public StudentService(
            StudentRepository studentRepository,
            RegistrationRequestRepository registrationRequestRepository,
            InvoiceService invoiceService, UserRepository userRepository, PasswordEncoder passwordEncoder, EnglishLevelRepository englishLevelRepository) {
        this.studentRepository = studentRepository;
        this.registrationRequestRepository = registrationRequestRepository;
        this.invoiceService = invoiceService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.englishLevelRepository = englishLevelRepository;
    }

    // HÀM TẠO TÀI KHOẢN HỌC VIÊN CHÍNH THỨC
    @Transactional
    public Student createStudentAccount(Integer registrationRequestId) {

        // 1. KIỂM TRA THANH TOÁN (Logic nghiệp vụ BẮT BUỘC)
        boolean isPaid = invoiceService.checkPaymentStatus(registrationRequestId);

        if (!isPaid) {
            // Ném ngoại lệ nếu hóa đơn chưa được thanh toán đầy đủ
            throw new IllegalStateException("Hóa đơn của Lead ID " + registrationRequestId +
                    " chưa được thanh toán đầy đủ. KHÔNG thể tạo tài khoản học viên.");
        }

        // 2. Lấy thông tin Lead (RegistrationRequest)
        RegistrationRequest lead = registrationRequestRepository.findById(registrationRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Lead ID: " + registrationRequestId));

        // 3. tạo tài khoản
        User user = new User();
        user.setEmail(lead.getEmail());
        user.setFullName(lead.getFullName());

        // Chuyển đổi dữ liệu từ Lead sang Student Entity

        String defaultPassword = "1234";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setRole("STUDENT");
        user.setEnabled(true);
        userRepository.save(user);
        // 4. TẠO HỒ SƠ STUDENT (Để quản lý học vụ)
        Student student = new Student();
        student.setFullname(lead.getFullName());
        student.setEmail(lead.getEmail());
        student.setPhone(lead.getPhone());
        student.setUsername(lead.getEmail());
        student.setRole("STUDENT");

        // Khởi tạo trình độ tiếng Anh mặc định cho học viên mới
        EnglishLevel newLevel = new EnglishLevel();
        student.setLevel(englishLevelRepository.save(newLevel));

        return studentRepository.save(student);
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
      Tạo mới hoặc lưu học viên (Có thể dùng hàm createStudentAccount thay thế nếu từ Lead)
     */
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    /*
      Cập nhật thông tin cá nhân của học viên (Update Profile)
     */
    @Transactional
    public Student updateStudentProfile(Integer id, Student newStudentData) {
        return studentRepository.findById(id)
                .map(student -> {
                    // Cần đảm bảo các Getter/Setter (Fullname, Phone, v.v.) khớp với Student Entity
                    // Giả định Student Entity của bạn có các phương thức setFullname, setPhone, v.v.
                    student.setFullname(newStudentData.getFullname());
                    student.setEmail(newStudentData.getEmail());
                    student.setPhone(newStudentData.getPhone());
                    student.setDateOfBirth(newStudentData.getDateOfBirth());
                    student.setAddress(newStudentData.getAddress());
                    return studentRepository.save(student);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy học viên có ID: " + id));
    }

    /*
      Xoá học viên theo ID
     */
    public void deleteStudent(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy học viên có ID: " + id);
        }
        studentRepository.deleteById(id);
    }
}