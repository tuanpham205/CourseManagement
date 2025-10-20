package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.model.Classroom;
import com.team5.quanlyhocvu.service.ClassroomService;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classrooms")
@CrossOrigin(origins = "*")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }
    /**
     *  Tạo Lớp học mới
     * POST /api/classrooms
     * (Cần bảo mật vai trò ADMIN)
     */
    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom) {
        Classroom newClassroom = classroomService.saveClassroom(classroom);
        return ResponseEntity.status(HttpStatus.CREATED).body(newClassroom);
    }

    /**
     * Lấy danh sách tất cả Lớp học
     * GET /api/classrooms
     */
    @GetMapping
    public ResponseEntity<List<Classroom>> getAllClassrooms() {
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }

    /**
     * Lấy thông tin Lớp học theo ID
     * GET /api/classrooms/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Integer id) {
        Classroom classroom = classroomService.getClassroomById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay Lop hoc voi ID: " + id));
        return ResponseEntity.ok(classroom);
    }

    /**
     * Cập nhật thông tin Lớp học
     * PUT /api/classrooms/{id}
     * (Cần bảo mật vai trò ADMIN)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Classroom> updateClassroom(@PathVariable Integer id, @RequestBody Classroom updatedClassroom) {
        // Đảm bảo ID được truyền vào đúng
        updatedClassroom.setId(id);
        Classroom classroom = classroomService.saveClassroom(updatedClassroom);
        return ResponseEntity.ok(classroom);
    }


    /**
     * Kiểm tra số slot còn trống của Lớp học
     * GET /api/classrooms/{id}/slots
     */
    @GetMapping("/{id}/slots")
    public ResponseEntity<Map<String, Object>> getRemainingSlots(@PathVariable Integer id) {

        // Gọi service để tính số slot còn trống
        int remainingSlots = classroomService.getRemainingSlots(id);
        // Trả về đối tượng JSON chứa thông tin slot
        return ResponseEntity.ok(
                Map.of(
                        "classroomId", id,
                        "remainingSlots", remainingSlots,
                        "isAvailable", remainingSlots > 0
                )
        );
    }
}