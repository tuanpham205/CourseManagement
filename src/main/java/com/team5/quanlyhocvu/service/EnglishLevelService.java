package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.Course;
import com.team5.quanlyhocvu.model.EnglishLevel;
import com.team5.quanlyhocvu.model.Student;
import com.team5.quanlyhocvu.repository.EnglishLevelRepository;
import com.team5.quanlyhocvu.repository.StudentRepository;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.HashMap;

@Service
public class EnglishLevelService {

    private final StudentRepository studentRepository;
    private final EnglishLevelRepository englishLevelRepository;

    // Ánh xạ thứ tự cấp độ VSTEP để so sánh thứ hạng
    private static final Map<String, Integer> VSTEP_LEVEL_MAP = new HashMap<>();
    static {
        VSTEP_LEVEL_MAP.put("A1", 1);
        VSTEP_LEVEL_MAP.put("A2", 2);
        VSTEP_LEVEL_MAP.put("B1", 3);
        VSTEP_LEVEL_MAP.put("B2", 4);
        VSTEP_LEVEL_MAP.put("C1", 5);
        VSTEP_LEVEL_MAP.put("C2", 6);
    }

    public EnglishLevelService(StudentRepository studentRepository, EnglishLevelRepository englishLevelRepository) {
        this.studentRepository = studentRepository;
        this.englishLevelRepository = englishLevelRepository;
    }

    // =======================================
    // 1. LOGIC CẬP NHẬT TRÌNH ĐỘ (ĐÃ SỬA)
    // =======================================

    @Transactional
    public EnglishLevel updateLevel(int studentId, Double ieltsBand, Integer toeicScore, String vstepLevel) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Course course = student.getCourse();

        EnglishLevel level = student.getLevel();

        if (level == null) {
            level = new EnglishLevel();
        }

        if (course == null) {
            level.setIeltsBand(ieltsBand);
            level.setToeicScore(toeicScore);
            level.setVstepLevel(vstepLevel);
        }

        else {
            String comparisonValue = null;

            if (course.getCourseName().equalsIgnoreCase("IELTS")) {
                level.setIeltsBand(ieltsBand);
                level.setToeicScore(null);
                level.setVstepLevel(null);
                if (ieltsBand != null) comparisonValue = String.format("IELTS %.1f", ieltsBand);
            }

            else if (course.getCourseName().equalsIgnoreCase("TOEIC")) {
                level.setToeicScore(toeicScore);
                level.setIeltsBand(null);
                level.setVstepLevel(null);
                if (toeicScore != null) comparisonValue = String.format("TOEIC %d", toeicScore);
            }

            else if (course.getCourseName().equalsIgnoreCase("VSTEP")) {
                level.setVstepLevel(vstepLevel);
                level.setIeltsBand(null);
                level.setToeicScore(null);
                if (vstepLevel != null) comparisonValue = String.format("VSTEP %s", vstepLevel);
            }
        }

        // Lưu EnglishLevel và cập nhật lại cho Student
        EnglishLevel savedLevel = englishLevelRepository.save(level);
        student.setLevel(savedLevel);
        studentRepository.save(student);

        return savedLevel;
    }

    // =======================================
    // 2. LOGIC KIỂM TRA ĐẠT CHUẨN ĐẦU VÀO (GIỮ NGUYÊN)
    // =======================================

    public boolean meetsInputStandard(EnglishLevel studentLevel, String requiredStandard) {

        if (studentLevel == null || requiredStandard == null || requiredStandard.isEmpty()) {
            return false;
        }

        String studentComparisonLevel = buildComparisonLevel(studentLevel);
        if (studentComparisonLevel == null) return false;

        String upperRequired = requiredStandard.toUpperCase();
        String upperStudent = studentComparisonLevel.toUpperCase();

        String type;
        String requiredValueStr;

        if (upperRequired.contains("IELTS")) {
            type = "IELTS";
            requiredValueStr = upperRequired.replace("IELTS", "").trim();
        } else if (upperRequired.contains("TOEIC")) {
            type = "TOEIC";
            requiredValueStr = upperRequired.replace("TOEIC", "").trim();
        } else if (upperRequired.contains("VSTEP")) {
            type = "VSTEP";
            requiredValueStr = upperRequired.replace("VSTEP", "").trim();
        } else {
            return false;
        }

        // Phải cùng loại
        if (!upperStudent.contains(type)) {
            return false;
        }

        try {
            if (type.equals("IELTS")) {
                double required = Double.parseDouble(requiredValueStr);
                double student = Double.parseDouble(upperStudent.replace("IELTS", "").trim());
                return student >= required;
            }

            if (type.equals("TOEIC")) {
                int required = Integer.parseInt(requiredValueStr);
                int student = Integer.parseInt(upperStudent.replace("TOEIC", "").trim());
                return student >= required;
            }

            if (type.equals("VSTEP")) {
                String studentVstep = upperStudent.replace("VSTEP", "").trim();
                return isVstepLevelSufficient(studentVstep, requiredValueStr);
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }


    /**
     * Kiểm tra xem cấp độ VSTEP của học viên có bằng hoặc cao hơn cấp độ yêu cầu không.
     * @param studentLevel Cấp độ VSTEP của học viên (Ví dụ: "C1").
     * @param requiredLevel Cấp độ VSTEP yêu cầu (Ví dụ: "B2").
     * @return true nếu studentLevel >= requiredLevel.
     */
    private boolean isVstepLevelSufficient(String studentLevel, String requiredLevel) {
        if (studentLevel == null || requiredLevel == null) return false;

        // Lấy giá trị số của cấp độ
        Integer studentRank = VSTEP_LEVEL_MAP.get(studentLevel.toUpperCase());
        Integer requiredRank = VSTEP_LEVEL_MAP.get(requiredLevel.toUpperCase());

        // Nếu một trong hai cấp độ không hợp lệ, trả về false
        if (studentRank == null || requiredRank == null) {
            return false;
        }

        // So sánh thứ hạng số
        return studentRank >= requiredRank;
    }
    private String buildComparisonLevel(EnglishLevel level) {
        if (level == null) return null;

        if (level.getIeltsBand() != null) {
            return String.format("IELTS %.1f", level.getIeltsBand());
        }

        if (level.getToeicScore() != null) {
            return String.format("TOEIC %d", level.getToeicScore());
        }

        if (level.getVstepLevel() != null) {
            return String.format("VSTEP %s", level.getVstepLevel());
        }

        return null;
    }

}