package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.config.JwtUtil;
import com.team5.quanlyhocvu.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    // Đăng ký User
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
        String email = registrationData.get("email");
        String password = registrationData.get("password");
        String fullName = registrationData.get("fullName");
        String role = registrationData.getOrDefault("role", "USER");

        if (email == null || password == null || fullName == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng cung cấp đầy đủ email, password và fullName"));
        }

        try {
            authService.registerNewUser(email, password, fullName, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Đăng ký thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // Đăng ký Admin (khởi tạo)
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Map<String, String> registrationData) {
        String email = registrationData.get("email");
        String password = registrationData.get("password");
        String fullName = registrationData.get("fullName");

        if (email == null || password == null || fullName == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng cung cấp đầy đủ email, password và fullName"));
        }

        try {
            authService.registerNewAdmin(email, password, fullName);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Đăng ký Admin thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng cung cấp email và password"));
        }

        try {
            String token = authService.login(email, password);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
    // Đổi pass

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader, // Lấy "Bearer <token>"
            @RequestBody Map<String, String> data
    ) {
        // 1. Trích xuất token từ Header (bỏ chữ "Bearer ")
        String token = authHeader.substring(7);

        // 2. Dùng JwtUtil của bạn để lấy email
        String email = jwtUtil.getUserNameFromJwtToken(token);

        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");

        //check rỗng thông tin
        if (email == null || oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Vui lòng nhập đủ mật khẩu cũ và mới");
        }

        try {
            //gọi service đổi pass
            authService.changePassword(email, oldPassword, newPassword);
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } catch (Exception e) {
            // Trả về lỗi nếu mật khẩu cũ sai hoặc không tìm thấy user
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}