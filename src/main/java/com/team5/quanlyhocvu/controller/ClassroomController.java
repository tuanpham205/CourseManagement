package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.model.Classroom;
import com.team5.quanlyhocvu.service.ClassroomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }
    //GET

    @GetMapping
    public ResponseEntity<List<Classroom>> getAll() {
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getById(@PathVariable Integer id) {
        Optional<Classroom> classroom = classroomService.getClassroomById(id);
        // Trả về 200 OK nếu tìm thấy, 404 Not Found nếu không tìm thấy
        return classroom.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- CRUD – CHỈ ADMIN ---

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Classroom> create(@RequestBody Classroom classroom) {
        Classroom createdClassroom = classroomService.createClassroom(classroom);
        // Chuẩn REST: Trả về Status 201 Created và URI của tài nguyên mới
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdClassroom.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdClassroom);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Classroom> update(
            @PathVariable Integer id,
            @RequestBody Classroom classroomDetails // Dùng tên biến mới để tránh nhầm lẫn
    ) {

        Classroom updatedClassroom = classroomService.updateClassroom(id, classroomDetails);

        return ResponseEntity.ok(updatedClassroom);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        classroomService.deleteClassroom(id);
        // Chuẩn REST: Trả về Status 204 No Content cho thao tác xóa thành công
        return ResponseEntity.noContent().build();
    }
}