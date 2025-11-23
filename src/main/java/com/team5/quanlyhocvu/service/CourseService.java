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

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ClassroomRepository classroomRepository;

    public CourseService(
            CourseRepository courseRepository,
            ClassroomRepository classroomRepository
    ) {
        this.courseRepository = courseRepository;
        this.classroomRepository = classroomRepository;
    }

    // CREATE
    @Transactional
    public Course saveCourse(Course course) {
        // Kiểm tra trung tên course
        if (courseRepository.findByCourseName(course.getCourseName()).isPresent()) {
            throw new DataConflictException(
                    "Khóa học với tên '" + course.getCourseName() + "' đã tồn tại."
            );
        }

        return courseRepository.save(course);
    }

    //  UPDATE
    @Transactional
    public Course updateCourse(Integer id, Course courseDetails) {
        Course existingCourse = findCourseById(id);

        // Nếu đổi tên → phải check trùng
        if (!existingCourse.getCourseName().equals(courseDetails.getCourseName())) {
            if (courseRepository.findByCourseName(courseDetails.getCourseName()).isPresent()) {
                throw new DataConflictException(
                        "Tên khóa học '" + courseDetails.getCourseName() + "' đã tồn tại."
                );
            }
        }

        existingCourse.setCourseName(courseDetails.getCourseName());
        return courseRepository.save(existingCourse);
    }

    //  DELETE
    @Transactional
    public void deleteCourse(Integer id) {
        Course course = findCourseById(id);

        List<Classroom> relatedClassrooms = classroomRepository.findByCourse_Id(id);
        if (!relatedClassrooms.isEmpty()) {
            throw new DataConflictException(
                    "Không thể xóa khóa học vì vẫn còn " + relatedClassrooms.size() + " lớp học đang hoạt động."
            );
        }

        courseRepository.delete(course);
    }

    //  READ
    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    public Course findCourseById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Khóa học với ID " + id + " không tồn tại.")
                );
    }

    public List<Course> searchCoursesByName(String name) {
        return courseRepository.findByCourseNameContainingIgnoreCase(name);
    }

    public List<Classroom> getClassroomsByCourse(Integer courseId) {
        findCourseById(courseId); // check tồn tại
        return classroomRepository.findByCourse_Id(courseId);
    }
}
