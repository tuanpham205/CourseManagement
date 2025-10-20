package com.team5.quanlyhocvu.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.time.LocalDate;

@MappedSuperclass
public abstract class Person {

    // 💡 Khóa chính
    @Id
    // Thường sử dụng GenerationType.IDENTITY hoặc SEQUENCE cho lớp cơ sở nếu các lớp con sử dụng chiến lược bảng riêng biệt.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullname;

    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    public Person(Integer id, String username, String password, String fullname,
                  String email, String phone, LocalDate dateOfBirth) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
    }
    public Person() {
    }

    /**
     * Phương thức trừu tượng để hiển thị chi tiết cụ thể cho từng vai trò (Teacher/Student).
     *Đã thêm @Transient để ngăn Hibernate cố gắng ánh xạ nó thành cột DB, vì nó là một giá trị TÍNH TOÁN/TRỪU TƯỢNG.
     */
    @Transient
    public abstract String getSpecificDetails();
    // Đảm bảo kiểu dữ liệu ID là Integer (theo quy tắc Spring Data JPA)
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}