package com.team5.quanlyhocvu.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "students")
public class Student extends Person {

    @ManyToOne
    @JoinColumn(name = "level_id", nullable = false)
    private EnglishLevel englishLevel;

    @Nationalized
    @Column(name = "address", columnDefinition = "NVARCHAR(500)")
    private String address;


    private LocalDate enrollmentDate;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom currentClassroom;

    public Student() {
    }

    public Student(int id, String username, String fullname, String email,
                   String phone, LocalDate dateOfBirth,
                   EnglishLevel englishLevel, String address, Classroom currentClassroom, LocalDate enrollmentDate) {

        super(id, username, fullname, email, phone, dateOfBirth, null);

        this.englishLevel = englishLevel;
        this.address = address;
        this.currentClassroom = currentClassroom;
        this.enrollmentDate = enrollmentDate;
    }

    public EnglishLevel getLevel() { return this.englishLevel; }
    public void setLevel(EnglishLevel level) { this.englishLevel = level; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public Classroom getCurrentClassroom() { return currentClassroom; }
    public void setCurrentClassroom(Classroom currentClassroom) { this.currentClassroom = currentClassroom; }

    @Override
    public String getSpecificDetails() {
        return "--- THÔNG TIN HỌC VIÊN ---\n" +
                "Vai trò: " + this.getRole() + "\n" +
                "Ngày nhập học: " + (enrollmentDate != null ? enrollmentDate : "Chưa cập nhật") + "\n" +
                "Level: " + (englishLevel != null ? englishLevel.getComparisonLevel() : "Chưa xác định") + "\n" +
                "Địa chỉ: " + address + "\n" +
                "Lớp hiện tại: " + (currentClassroom != null ? currentClassroom.getClassName() : "Chưa phân lớp");
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", fullname='" + getFullname() + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                ", level='" + (englishLevel != null ? englishLevel.getComparisonLevel() : "null") + '\'' +
                ", classroom=" + (currentClassroom != null ? currentClassroom.getClassName() : "null") +
                '}';
    }

    public Course getCourse() {
        if (this.currentClassroom == null) {
            return null;
        }
        return this.currentClassroom.getCourse();
    }

    public Double getIeltsBand() { return englishLevel != null ? englishLevel.getIeltsBand() : null; }
    public Integer getToeicScore() { return englishLevel != null ? englishLevel.getToeicScore() : null; }
    public String getVstepLevel() { return englishLevel != null ? englishLevel.getVstepLevel() : null; }
}