package com.team5.quanlyhocvu.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student extends Person {
    private String level;
    private String address;
    private Integer currentClassroomId;

    public Student() {
    }

    public Student(int id, String username, String password, String fullname, String email,
                   String phone, LocalDate dateOfBirth,
                   String level, String address, Integer currentClassroomId) {
        super(id, username, password, fullname, email, phone, dateOfBirth);
        this.level = level;
        this.address = address;
        this.currentClassroomId = currentClassroomId;
    }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getCurrentClassroomId() { return currentClassroomId; }
    public void setCurrentClassroomId(Integer currentClassroomId) { this.currentClassroomId = currentClassroomId; }

    @Override
    public String getSpecificDetails() {
        return "--- THÔNG TIN HỌC VIÊN ---\n" +
                "Level: " + level + "\n" +
                "Địa chỉ: " + address + "\n" +
                "ID Lớp hiện tại: " + (currentClassroomId != null ? currentClassroomId : "Chưa phân lớp");
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", fullname='" + getFullname() + '\'' +
                ", level='" + level + '\'' +
                ", classroomId=" + (currentClassroomId != null ? currentClassroomId : "null") +
                '}';
    }
}