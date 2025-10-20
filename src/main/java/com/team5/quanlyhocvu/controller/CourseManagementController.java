package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.model.Course;
import com.team5.quanlyhocvu.service.CourseService;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/courses")
@CrossOrigin(origins = "*")
public class CourseManagementController {

    private final CourseService courseService;

    public CourseManagementController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course newCourse = courseService.saveCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCourse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Integer id, @RequestBody Course courseDetails) {
        // Logic updateCourse trong Service trả về Optional<Course>
        Course updatedCourse = courseService.updateCourse(id, courseDetails)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay Khoa hoc de cap nhat co ID: " + id));
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}