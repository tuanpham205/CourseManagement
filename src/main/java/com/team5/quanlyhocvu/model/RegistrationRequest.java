package com.team5.quanlyhocvu.model;

import com.team5.quanlyhocvu.model.enums.RequestStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "registration_requests")
public class RegistrationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    private String email;
    private String address;

    private Double ieltsBand;
    private Integer toeicScore;
    private String vstepLevel;

    private String desiredCourseName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.NEW; // Mặc định là NEW

    // THÊM TRƯỜNG GHI CHÚ CỦA ADMIN
    @Column(columnDefinition = "TEXT")
    private String adminNote;

    public RegistrationRequest() {}

    public RegistrationRequest(String fullName, String phone, String email, String address,
                               String desiredCourseName) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.desiredCourseName = desiredCourseName;
        this.status = RequestStatus.NEW;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getIeltsBand() {
        return ieltsBand;
    }

    public void setIeltsBand(Double ieltsBand) {
        this.ieltsBand = ieltsBand;
    }

    public Integer getToeicScore() {
        return toeicScore;
    }

    public void setToeicScore(Integer toeicScore) {
        this.toeicScore = toeicScore;
    }

    public String getVstepLevel() {
        return vstepLevel;
    }

    public void setVstepLevel(String vstepLevel) {
        this.vstepLevel = vstepLevel;
    }

    public String getDesiredCourseName() {
        return desiredCourseName;
    }

    public void setDesiredCourseName(String desiredCourseName) {
        this.desiredCourseName = desiredCourseName;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", ieltsBand=" + ieltsBand +
                ", toeicScore=" + toeicScore +
                ", vstepLevel='" + vstepLevel + '\'' +
                ", desiredCourseName='" + desiredCourseName + '\'' +
                ", status=" + status +
                ", adminNote='" + adminNote + '\'' +
                '}';
    }
}