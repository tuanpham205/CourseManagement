package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.model.Student;
import com.team5.quanlyhocvu.service.StudentService;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")

public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return ResponseEntity.ok(student);
    }


    @PutMapping("/{id}/profile")

    public ResponseEntity<Student> updateStudentProfile(@PathVariable int id, @RequestBody Student studentProfileUpdate) {
        Student updatedStudent = studentService.updateStudentProfile(id, studentProfileUpdate);
        return ResponseEntity.ok(updatedStudent);
    }

}