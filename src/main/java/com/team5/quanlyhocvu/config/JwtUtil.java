package com.team5.quanlyhocvu.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    // Sử dụng Logger chuẩn của Spring/SLF4J để ghi nhật ký lỗi rõ ràng
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // Tạo Key từ secret, đảm bảo sử dụng UTF-8 để nhất quán
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // ===============================================
    // PHẦN 1: TẠO TOKEN
    // ===============================================
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ===============================================
    // PHẦN 2: GIẢI MÃ VÀ XÁC THỰC
    // ===============================================
    public String getUserNameFromJwtToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            logger.error("Error extracting username from token.", e);
            throw new JwtException("Token is invalid or expired.");
        }
    }

    // Phương thức xác thực token (sẽ bắt lỗi và log ra console)
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            // Lỗi chữ ký: Secret Key dùng để xác thực không khớp với Secret Key dùng để ký token
            logger.error("Invalid JWT signature (Secret Key mismatch): {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // Lỗi hết hạn: Token đã hết thời gian sử dụng
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            // Lỗi định dạng: Cấu trúc token không đúng (ví dụ: bị thay đổi)
            logger.error("Invalid JWT token format: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}