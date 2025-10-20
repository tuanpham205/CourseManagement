package com.team5.quanlyhocvu.service.exception;
//400 Bad Request
/**
 * Xu ly cac loi nghiep vu lien quan den dang ky, phan cong.
 * Vi du: lop day, khong du trinh do, giao vien khong co chuyen mon phu hop.
 */
public class RegistrationException extends RuntimeException {
    public RegistrationException(String message) {
        super(message);
    }
}