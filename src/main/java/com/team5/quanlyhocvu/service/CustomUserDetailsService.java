package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.User;
import com.team5.quanlyhocvu.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Phương thức bắt buộc của UserDetailsService.
     * Được gọi bởi AuthenticationManager trong quá trình Đăng nhập.
     * @param email (username) được gửi từ form đăng nhập.
     * @return UserDetails object để Spring so sánh mật khẩu.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Truy vấn Database để tìm User theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email)
                );

        // 2. Chuyển đổi Role thành danh sách GrantedAuthority
        // Lưu ý: Spring Security yêu cầu Role phải có tiền tố "ROLE_"
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );

        // 3. Xây dựng và trả về đối tượng UserDetails của Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())      // Mật khẩu (đã hash) từ DB
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())       // Trạng thái kích hoạt
                .build();
    }
}