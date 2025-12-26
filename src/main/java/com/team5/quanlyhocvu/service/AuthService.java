package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.config.JwtUtil;
import com.team5.quanlyhocvu.model.Admin;
import com.team5.quanlyhocvu.model.User;
import com.team5.quanlyhocvu.repository.AdminRepository;
import com.team5.quanlyhocvu.repository.UserRepository;
import com.team5.quanlyhocvu.service.exception.DataConflictException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.AuthenticationException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, AdminRepository adminRepository,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // Logic dùng chung cho các loại tài khoản mới
    @Transactional
    public void registerNewUser(String email, String password, String fullName, String role) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DataConflictException("Email " + email + " đã được sử dụng.");
        }
        String finalRole = (role != null ? role.toUpperCase() : "USER").replace("ROLE_", "");
        //Lưu vào user để đăng nhập
        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));

        newUser.setRole(finalRole);
        newUser.setEnabled(true);

        userRepository.save(newUser);
        //Lưu vào admin để quản lý riêng nếu sau này có thêm thuộc tính
        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setFullName(fullName);
        adminRepository.save(admin);
    }

    // Logic riêng cho ADMIN
    @Transactional
    public void registerNewAdmin(String email, String password, String fullName) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DataConflictException("Email " + email + " đã được sử dụng.");
        }

        User newAdmin = new User();
        newAdmin.setFullName(fullName);
        newAdmin.setEmail(email);
        newAdmin.setPassword(passwordEncoder.encode(password));
        newAdmin.setRole("ADMIN");
        newAdmin.setEnabled(true);

        userRepository.save(newAdmin);
    }


    public String login(String email, String password) {
        try {
            // 1. Thực hiện xác thực (authentication)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // 2. Tạo Token JWT
            String jwt = jwtUtil.generateToken(authentication);

            return jwt;
        } catch (AuthenticationException e) {
            // Ném ra exception khi tên đăng nhập hoặc mật khẩu không đúng
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không đúng.", e);
        }
    }

    //Tính năng đổi pass dùng chung
    @Transactional
    public void changePassword(String email,String oldPassword, String newPassword) {
        // check email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));
        //check pass
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }
        //mã hóa pass mới và lưu
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    }
