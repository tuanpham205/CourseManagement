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

    /**
     * Tạo mới hoặc cập nhật lớp học.
     * Xử lý kiểm tra trùng tên lớp (Classroom Name) khi tạo mới.
     *
     * @param classroom Đối tượng Classroom cần lưu.
     * @return Classroom đã được lưu vào database.
     * @throws DataConflictException Nếu tên lớp đã tồn tại.
     */
    @Transactional
    public Classroom createClassroom(Classroom classroom) {
        // Chỉ kiểm tra trùng tên khi ID là null (tức là tạo mới)
        if (classroom.getId() == null && classroomRepository.findByClassName(classroom.getClassName()).isPresent()) {
            throw new DataConflictException("Tên lớp học '" + classroom.getClassName() + "' đã tồn tại.");
        }
        // Thêm kiểm tra trùng tên khi update:
        // Nếu là update (ID != null), phải đảm bảo tên lớp không trùng với lớp khác
        if (classroom.getId() != null) {
            Optional<Classroom> existingByName = classroomRepository.findByClassName(classroom.getClassName());
            if (existingByName.isPresent() && !existingByName.get().getId().equals(classroom.getId())) {
                throw new DataConflictException("Tên lớp học '" + classroom.getClassName() + "' đã được sử dụng cho lớp khác.");
            }
        }
        return classroomRepository.save(classroom);
    }

    /**
     * Cập nhật thông tin lớp học theo ID.
     *
     * @param id ID của lớp học cần cập nhật.
     * @param classroomDetails Dữ liệu mới của lớp học.
     * @return Classroom đã được cập nhật.
     * @throws ResourceNotFoundException Nếu lớp học không tồn tại.
     * @throws DataConflictException Nếu tên lớp bị trùng lặp.
     */
    @Transactional
    public Classroom updateClassroom(Integer id, Classroom classroomDetails) {
        Classroom existingClassroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lớp học với ID " + id + " không tồn tại."));

        // 1. Kiểm tra trùng tên (như trong createClassroom)
        Optional<Classroom> existingByName = classroomRepository.findByClassName(classroomDetails.getClassName());
        if (existingByName.isPresent() && !existingByName.get().getId().equals(id)) {
            throw new DataConflictException("Tên lớp học '" + classroomDetails.getClassName() + "' đã được sử dụng cho lớp khác.");
        }

        // 2. Cập nhật các trường dữ liệu
        existingClassroom.setClassName(classroomDetails.getClassName());
        existingClassroom.setMaxCapacity(classroomDetails.getMaxCapacity());
        // Thêm các trường khác cần update...

        return classroomRepository.save(existingClassroom);
    }

    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    public Optional<Classroom> getClassroomById(Integer id) {
        return classroomRepository.findById(id);
    }

    /**
     * Tìm lớp học theo tên.
     */
    public Optional<Classroom> getClassroomByName(String name) {
        return classroomRepository.findByClassName(name);
    }

    /**
     * Xóa lớp học theo ID.
     *
     * @param id ID của lớp học cần xóa.
     * @throws ResourceNotFoundException Nếu lớp học không tồn tại.
     */
    @Transactional
    public void deleteClassroom(Integer id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lớp học với ID " + id + " không tồn tại."));

        // **Nên thêm kiểm tra ràng buộc ở đây:**
        // if (studentRepository.existsByCurrentClassroomId(id)) {
        //    throw new DataConflictException("Không thể xóa lớp học vì vẫn còn học sinh đang theo học.");
        // }

        classroomRepository.delete(classroom);
    }


    // --- Business Logic / Utility Methods ---

    /**
     * Tính số slot còn trống của lớp học.
     *
     * @param classroomId ID của lớp học.
     * @return Số slot còn trống.
     * @throws ResourceNotFoundException Nếu lớp học không tồn tại.
     */
    public int getRemainingSlots(Integer classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Lớp học với ID " + classroomId + " không tồn tại."));

        long currentStudents = studentRepository.countByCurrentClassroom_Id(classroomId);

        return (int) (classroom.getMaxCapacity() - currentStudents);
    }

    /**
     * Kiểm tra lớp học còn chỗ trống hay không.
     *
     * @param classroomId ID của lớp học.
     * @return True nếu còn trống, False nếu đầy.
     */
    public boolean checkSlotAvailability(Integer classroomId) {
        // Tận dụng phương thức getRemainingSlots và xử lý lỗi bên trong nó
        // Nếu lớp học không tồn tại, nó sẽ tự động ném ra ResourceNotFoundException
        return getRemainingSlots(classroomId) > 0;
    }
}