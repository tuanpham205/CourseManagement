package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.Teacher;
import com.team5.quanlyhocvu.model.Classroom;
import com.team5.quanlyhocvu.repository.TeacherRepository;
import com.team5.quanlyhocvu.repository.ClassroomRepository;
import com.team5.quanlyhocvu.service.exception.DataConflictException;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;

    public TeacherService(TeacherRepository teacherRepository, ClassroomRepository classroomRepository) {
        this.teacherRepository = teacherRepository;
        this.classroomRepository = classroomRepository;
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> getTeacherById(int id) {
        return teacherRepository.findById(id);
    }

    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher updateTeacherProfile(int id, Teacher updatedProfileData) {
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay giao vien voi ID: " + id));

        // Cập nhật thông tin cơ bản
        existingTeacher.setFullname(updatedProfileData.getFullname());
        existingTeacher.setEmail(updatedProfileData.getEmail());
        existingTeacher.setPhone(updatedProfileData.getPhone());

        // Lưu ý: Username và Password cần được xử lý qua phương thức riêng biệt

        // Cập nhật chuyên môn (Giả định giáo viên có thể tự cập nhật)
        existingTeacher.setSpecialization(updatedProfileData.getSpecialization());

        return teacherRepository.save(existingTeacher);
    }

    public List<Classroom> getClassroomsByTeacherId(int teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay giao vien voi ID: " + teacherId));

        return classroomRepository.findAllById(teacher.getClassroomIds());
    }
}