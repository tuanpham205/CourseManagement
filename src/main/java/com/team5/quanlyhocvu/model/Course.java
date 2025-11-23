package com.team5.quanlyhocvu.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String courseName;

    public Course() {}

    public Course(Integer id, String courseName) {
        this.id = id;
        this.courseName = courseName;
    }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    @Override
    public String toString() {
        return "Course{id=" + id + ", courseName='" + courseName + "'}";
    }


}