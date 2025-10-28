package com.team5.quanlyhocvu.model;

import jakarta.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "classrooms")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String className;
    private int maxCapacity;
    private int lessonCount;
    private String inputStandard;
    private String outputStandard;

    private Integer courseId;

    private Integer teacherId;

    public Classroom() {}
    public Classroom(String name, int capacity, int lessonCount, String input, String output) {
        this.className = name;
        this.maxCapacity = capacity;
        this.lessonCount = lessonCount;
        this.inputStandard = input;
        this.outputStandard = output;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    public int getLessonCount() { return lessonCount; }
    public void setLessonCount(int lessonCount) { this.lessonCount = lessonCount; }

    public String getInputStandard() { return inputStandard; }
    public void setInputStandard(String inputStandard) { this.inputStandard = inputStandard; }

    public String getOutputStandard() { return outputStandard; }
    public void setOutputStandard(String outputStandard) { this.outputStandard = outputStandard; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }

}