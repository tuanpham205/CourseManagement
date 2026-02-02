package com.team5.quanlyhocvu.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.Nationalized;

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

    private String roomNumber;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Integer teacherId;

    public Classroom() {}

    public Classroom(String name, int capacity, int lessonCount, String input, String output, String roomNumber, LocalDate startDate, LocalDate endDate) {
        this.className = name;
        this.maxCapacity = capacity;
        this.lessonCount = lessonCount;
        this.inputStandard = input;
        this.outputStandard = output;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }
}