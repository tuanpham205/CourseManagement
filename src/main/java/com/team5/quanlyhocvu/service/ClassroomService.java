package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.Classroom;
import com.team5.quanlyhocvu.repository.ClassroomRepository;
import com.team5.quanlyhocvu.repository.StudentRepository;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import com.team5.quanlyhocvu.service.exception.DataConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;

    public ClassroomService(ClassroomRepository classroomRepository, StudentRepository studentRepository) {
        this.classroomRepository = classroomRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public Classroom saveClassroom(Classroom classroom) {
        if (classroom.getId() == null && classroomRepository.findByClassName(classroom.getClassName()).isPresent()) {
            throw new DataConflictException("Tên lớp học '" + classroom.getClassName() + "' đã tồn tại.");
        }
        return classroomRepository.save(classroom);
    }

    public Optional<Classroom> getClassroomById(Integer id) {
        return classroomRepository.findById(id);
    }

    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    public Optional<Classroom> getClassroomByName(String name) {
        return classroomRepository.findByClassName(name);
    }

    /**
     * Tính số slot còn trống của lớp học.
     */
    public int getRemainingSlots(Integer classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Lớp học với ID " + classroomId + " không tồn tại."));

        long currentStudents = studentRepository.countByCurrentClassroomId(classroomId);

        return (int) (classroom.getMaxCapacity() - currentStudents);
    }

    /**
     * Kiểm tra lớp học còn chỗ trống hay không.
     */
    public boolean checkSlotAvailability(Integer classroomId) {
        return getRemainingSlots(classroomId) > 0;
    }

}