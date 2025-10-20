package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.model.Course;
import com.team5.quanlyhocvu.service.CourseService;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.findAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Integer id) {
        // findCourseById trả về Course, ném ResourceNotFoundException nếu không tìm thấy
        Course course = courseService.findCourseById(id);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCoursesByName(@RequestParam String name) {
        List<Course> courses = courseService.searchCoursesByName(name);
        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(courses);
    }
}