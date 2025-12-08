package com.team5.quanlyhocvu.model.enums;

public enum PaymentMethod {

    CASH("Tiền mặt"),
    BANK_TRANSFER("Chuyển khoản Ngân hàng");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}