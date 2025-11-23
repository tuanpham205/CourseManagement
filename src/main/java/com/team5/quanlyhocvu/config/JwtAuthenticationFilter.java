package com.team5.quanlyhocvu.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.JwtException; // Thêm import cho ngoại lệ JWT

import java.io.IOException;

/**
 * Bộ lọc JWT để xác thực token trong mỗi request.
 * Đảm bảo token được kiểm tra một lần duy nhất trên mỗi request.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // Sử dụng Dependency Injection thông qua Constructor
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = parseJwt(request);

        // Trường hợp 1: Không có JWT
        // Thoát sớm để cho phép các endpoint công khai (permitAll) được xử lý bởi SecurityConfig.
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Trường hợp 2: Có JWT, tiến hành xác thực và phân quyền

            // 1. Xác thực Token (JwtUtil sẽ ném JwtException nếu chữ ký, thời hạn lỗi)
            if (jwtUtil.validateToken(jwt)) {

                // 2. Lấy tên người dùng và UserDetails
                String username = jwtUtil.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 3. Tạo đối tượng Authentication
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 4. Đặt Authentication vào Security Context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication successful for user: {}", username);
            }
        } catch (JwtException e) {
            // Trường hợp 3: Token không hợp lệ (Signature, Expired, Malformed)
            logger.warn("JWT Validation failed for request: {}", e.getMessage());

            // Lưu ý: Không ném ngoại lệ ở đây. Việc không đặt Authentication vào
            // Security Context sẽ khiến SecurityConfig trả về 401/403 sau đó.
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            // Trường hợp 4: Các lỗi khác (ví dụ: User not found trong UserDetailsService)
            logger.error("Other Authentication Error:", e);
            SecurityContextHolder.clearContext();
        }

        // Chuyển tiếp request đến các Filter tiếp theo (bao gồm cả Authorization Filter)
        filterChain.doFilter(request, response);
    }

    /**
     * Trích xuất JWT từ Header "Authorization: Bearer <token>"
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}