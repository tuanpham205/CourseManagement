package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.model.Teacher;
import com.team5.quanlyhocvu.service.TeacherService;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    // LẤY GIÁO VIÊN THEO ID (Xem profile)
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable int id) {
        return teacherService.getTeacherById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay giao vien voi ID: " + id));
    }

    // LẤY CÁC LỚP MÀ GIÁO VIÊN ĐANG DẠY
    @GetMapping("/{id}/classrooms")
    public ResponseEntity<?> getTeacherClassrooms(@PathVariable int id) {
        var classrooms = teacherService.getClassroomsByTeacherId(id);
        return ResponseEntity.ok(classrooms);
    }
    // CẬP NHẬT PROFILE CÁ NHÂN (chỉ cho Giáo viên đó)
    @PutMapping("/{id}/profile")
    public ResponseEntity<Teacher> updateTeacherProfile(@PathVariable int id, @RequestBody Teacher updatedProfileData) {
        Teacher teacher = teacherService.updateTeacherProfile(id, updatedProfileData);
        return ResponseEntity.ok(teacher);
    }

}