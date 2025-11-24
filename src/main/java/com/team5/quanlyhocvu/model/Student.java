package com.team5.quanlyhocvu.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student extends Person {

    @OneToOne
    @JoinColumn(name = "level_id")
    private EnglishLevel englishLevel;

    private String address;

    @OneToOne
    @JoinColumn(name = "classroom_id")
    private Classroom currentClassroom;

    public Student() {
    }

    public Student(int id, String username, String password, String fullname, String email,
                   String phone, LocalDate dateOfBirth,
                   EnglishLevel englishLevel, String address, Classroom currentClassroom) {

        super(id, username, password, fullname, email, phone, dateOfBirth, null);

        this.englishLevel = englishLevel;
        this.address = address;
        this.currentClassroom = currentClassroom;
    }

    public EnglishLevel getLevel() { return this.englishLevel; }
    public void setLevel(EnglishLevel level) { this.englishLevel = level; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Classroom getCurrentClassroom() { return currentClassroom; }
    public void setCurrentClassroom(Classroom currentClassroom) { this.currentClassroom = currentClassroom; }

    @Override
    public String getSpecificDetails() {

        return "--- THÔNG TIN HỌC VIÊN ---\n" +
                "Vai trò: " + this.getRole() + "\n" +
                "Level: " + englishLevel + "\n" +
                "Địa chỉ: " + address + "\n" +
                "ID Lớp hiện tại: " + (currentClassroom != null ? currentClassroom : "Chưa phân lớp");
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", fullname='" + getFullname() + '\'' +
                ", role='" + this.getRole() + '\'' +
                ", level='" + englishLevel + '\'' +
                ", classroomId=" + (currentClassroom != null ? currentClassroom : "null") +
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
