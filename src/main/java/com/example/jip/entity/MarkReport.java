package com.example.jip.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mark_report")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    Student student;
    @Column(name = "presentation")
    BigDecimal presentation;
    @Column(name = "script_presentation")
    BigDecimal script_presentation;
    @Column(name = "softskill")
    BigDecimal softskill;
    @OneToMany(mappedBy = "markReport", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MarkReportExam> markReportExams = new ArrayList<>();
    @Column(name = "avg_exam_mark")
    BigDecimal avg_exam_mark;
    @Column(name = "midterm_exam")
    BigDecimal middle_exam;
    @Column(name = "final_exam")
    BigDecimal final_exam;
    @Column(name = "skill")
    BigDecimal skill;
    @Column(name = "attitude")
    BigDecimal attitude;
    @Column(name = "final_mark")
    BigDecimal final_mark;
    @Column(name = "comment")
    String comment;
}
