package com.team5.quanlyhocvu.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Phương thức này được gọi khi người dùng cố gắng truy cập một tài nguyên bảo mật
     * mà không có đủ thông tin xác thực (ví dụ: không có JWT hoặc JWT không hợp lệ).
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Trả về lỗi 401 Unauthorized thay vì 403 Forbidden, giúp tách biệt lỗi xác thực
        // Điều này rất quan trọng để giải quyết lỗi 403 không rõ nguyên nhân.
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Unauthorized\"}");
    }
    }