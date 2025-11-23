package com.team5.quanlyhocvu.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
public class Teacher extends Person {
    private String specialization;
    @ElementCollection // Tạo một bảng riêng để lưu danh sách ID lớp
    @CollectionTable(name = "teacher_classrooms", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "classroom_id")
    private List<Integer> classroomIds;

    public Teacher() {
        this.classroomIds = new ArrayList<>();
    }

    public Teacher(int id, String username, String password, String fullname,
                   String email, String phone, LocalDate dateOfBirth,
                   String specialization) {
        super(id, username, password, fullname, email, phone, dateOfBirth, null);
        this.specialization = specialization;
        this.classroomIds = new ArrayList<>();
    }

    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public List<Integer> getClassroomIds() {
        return classroomIds;
    }

    public void setClassroomIds(List<Integer> classroomIds) {
        this.classroomIds = classroomIds;
    }

    // Thêm ID lớp học vào danh sách phụ trách.
    public void addClassroomId(Integer classroomId) {
        if (classroomId != null && !this.classroomIds.contains(classroomId)) {
            this.classroomIds.add(classroomId);
        }
    }

    // Loại bỏ ID lớp học khỏi danh sách phụ trách.
    public void removeClassroomId(Integer classroomId) {
        if (classroomId != null) {
            this.classroomIds.remove(classroomId);
        }
    }

    @Transient // Yêu cầu Hibernate bỏ qua thuộc tính này vì nó là getter tổng hợp cho API
    @Override
    public String getSpecificDetails() {
        return "--- THÔNG TIN GIÁO VIÊN ---\n" +
                "Vai trò: " + this.getRole() + "\n" +
                "Chuyên môn: " + this.specialization + "\n" +
                "Số lớp đang phụ trách: " + this.classroomIds.size();
    }

}