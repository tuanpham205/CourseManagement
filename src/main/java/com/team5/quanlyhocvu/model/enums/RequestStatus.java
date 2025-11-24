package com.team5.quanlyhocvu.model.enums;

public enum RequestStatus {
    NEW,        // Yêu cầu mới, chưa liên hệ
    CONTACTED,  // Đã liên hệ lần đầu
    QUALIFIED,  // Đủ điều kiện, chờ tạo tài khoản Student chính thức
    CLOSED      // Đã đóng, không chuyển đổi
}