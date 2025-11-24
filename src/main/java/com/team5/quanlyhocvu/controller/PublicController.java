package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.model.RegistrationRequest;
import com.team5.quanlyhocvu.service.RegistrationRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicController {

    private final RegistrationRequestService registrationRequestService;

    public PublicController(RegistrationRequestService registrationRequestService) {
        this.registrationRequestService = registrationRequestService;
    }

    /**
     * API PUBLIC: Gửi yêu cầu tư vấn/đăng ký từ form của học viên tiềm năng.
     * @param request JSON Body chứa thông tin đăng ký.
     */
    @PostMapping("/consultation/request")
    public ResponseEntity<RegistrationRequest> submitConsultationRequest(@RequestBody RegistrationRequest request) {
        RegistrationRequest savedRequest = registrationRequestService.createRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
    }
}