package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.RegistrationRequest;
import com.team5.quanlyhocvu.model.enums.RequestStatus;
import com.team5.quanlyhocvu.repository.RegistrationRequestRepository;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RegistrationRequestService {

    private final RegistrationRequestRepository repository;

    public RegistrationRequestService(RegistrationRequestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RegistrationRequest createRequest(RegistrationRequest req) {
        if (req.getStatus() == null) {
            req.setStatus(RequestStatus.NEW);
        }
        return repository.save(req);
    }

    public List<RegistrationRequest> getAllRequests() {
        return repository.findAll();
    }

    // CẬP NHẬT YÊU CẦU & BỔ SUNG LEVEL (CHO ADMIN)
    @Transactional
    public RegistrationRequest updateRequest(Integer id, Map<String, Object> updateFields) {
        RegistrationRequest request = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu có ID: " + id));
        // Cập nhật trạng thái
        if (updateFields.containsKey("status")) {
            try {
                String statusStr = (String) updateFields.get("status");
                request.setStatus(RequestStatus.valueOf(statusStr.toUpperCase()));
            } catch (Exception e) {
                throw new IllegalArgumentException("Trạng thái không hợp lệ.");
            }
        }

        // Cập nhật Ghi chú của Admin
        if (updateFields.containsKey("adminNote")) {
            request.setAdminNote((String) updateFields.get("adminNote"));
        }

        Object ieltsObj = updateFields.get("ieltsBand");
        if (ieltsObj instanceof Number) {
            request.setIeltsBand(((Number) ieltsObj).doubleValue());
        }

        Object toeicObj = updateFields.get("toeicScore");
        if (toeicObj instanceof Number) {
            request.setToeicScore(((Number) toeicObj).intValue());
        }

        Object vstepObj = updateFields.get("vstepLevel");
        if (vstepObj instanceof String) {
            request.setVstepLevel((String) vstepObj);
        }
        // Logic tự động chuyển trạng thái (chỉ chuyển từ NEW sang CONTACTED nếu chưa được set status khác)
        if (!updateFields.containsKey("status") && request.getStatus() == RequestStatus.NEW) {
            request.setStatus(RequestStatus.CONTACTED);
        }

        return repository.save(request);
    }
}