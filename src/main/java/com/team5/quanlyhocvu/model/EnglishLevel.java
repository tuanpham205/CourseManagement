package com.team5.quanlyhocvu.model;
import jakarta.persistence.*;

@Entity
@Table(name ="english_levels")

public class EnglishLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Double ieltsBand;
    private Integer toeicScore;
    private String vstepLevel;
    public EnglishLevel() {}
    public Double getIeltsBand() {return this.ieltsBand;}
    public Integer getToeicScore() {return this.toeicScore;}
    public String getVstepLevel() {return this.vstepLevel;}
    public void setVstepLevel(String vstepLevel) {this.vstepLevel = vstepLevel;}
    public void setIeltsBand(Double ieltsBand) {this.ieltsBand = ieltsBand;}
    public void setToeicScore(Integer toeicScore) {this.toeicScore = toeicScore;}
    public String getComparisonLevel() {
        if (this.ieltsBand != null) {
            // Định dạng IELTS: "IELTS 6.0"
            return String.format("IELTS %.1f", this.ieltsBand);
        }

        if (this.toeicScore != null) {
            // Định dạng TOEIC: "TOEIC 750"
            return String.format("TOEIC %d", this.toeicScore);
        }

        if (this.vstepLevel != null) {
            // Định dạng VSTEP: "VSTEP B2"
            return String.format("VSTEP %s", this.vstepLevel);
        }


        return null;
    }
}

