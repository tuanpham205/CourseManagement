package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.Course;
import com.team5.quanlyhocvu.model.Classroom;
import com.team5.quanlyhocvu.repository.CourseRepository;
import com.team5.quanlyhocvu.repository.ClassroomRepository;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import com.team5.quanlyhocvu.service.exception.DataConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ClassroomRepository classroomRepository;

    public CourseService(CourseRepository courseRepository, ClassroomRepository classroomRepository) {
        this.courseRepository = courseRepository;
        this.classroomRepository = classroomRepository;
    }

    @Transactional
    public Course saveCourse(Course course) {
        if (course.getId() == null || course.getId() == 0) {
            Optional<Course> existingCourse = courseRepository.findByCourseName(course.getCourseName());

            if (existingCourse.isPresent()) {
                throw new DataConflictException("Khoa hoc voi ten '" + course.getCourseName() + "' da ton tai.");
            }
        }
        return courseRepository.save(course);
    }

    @Transactional
    public Optional<Course> updateCourse(Integer id, Course courseDetails) {
        return courseRepository.findById(id).map(existingCourse -> {

            if (!existingCourse.getCourseName().equals(courseDetails.getCourseName())) {
                if (courseRepository.findByCourseName(courseDetails.getCourseName()).isPresent()) {
                    throw new DataConflictException("Ten khoa hoc '" + courseDetails.getCourseName() + "' da ton tai.");
                }
            }

            // Cập nhật trường duy nhất còn lại
            existingCourse.setCourseName(courseDetails.getCourseName());

            return courseRepository.save(existingCourse);
        });
    }

    @Transactional
    public void deleteCourse(Integer id) {
        Course course = findCourseById(id);
        List<Classroom> relatedClassrooms = classroomRepository.findByCourseId(id);

        if (!relatedClassrooms.isEmpty()) {
            throw new DataConflictException("Khong the xoa khoa hoc vi van con " + relatedClassrooms.size() + " lop hoc dang hoat dong.");
        }
        courseRepository.delete(course);
    }

    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> searchCoursesByName(String name) {
        return courseRepository.findByCourseNameContainingIgnoreCase(name);
    }

    public Optional<Course> getCourseById(Integer id) {
        return courseRepository.findById(id);
    }

    public Course findCourseById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khoa hoc voi ID " + id + " khong ton tai."));
    }

    public List<Classroom> getClassroomsByCourse(Integer courseId) {
        findCourseById(courseId);
        return classroomRepository.findByCourseId(courseId);
    }
}