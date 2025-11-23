package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
}
