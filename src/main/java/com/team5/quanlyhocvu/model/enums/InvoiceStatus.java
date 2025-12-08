package com.team5.quanlyhocvu.model.enums; // Đảm bảo đúng package

public enum InvoiceStatus {

    PENDING("Chờ thanh toán"),
    PAID("Đã thanh toán đầy đủ"),
    FAILED("Thanh toán thất bại"),
    REFUNDED("Đã hoàn tiền"),
    CANCELLED("Đã hủy hóa đơn");

    private final String description;
    InvoiceStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}