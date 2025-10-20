package com.team5.quanlyhocvu.service.exception;
//409 Conflict
/**
 * Xu ly khi co xung dot du lieu (vi pham UNIQUE constraint).
 * Vi du: Dang ky Email da ton tai.
 */
public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}