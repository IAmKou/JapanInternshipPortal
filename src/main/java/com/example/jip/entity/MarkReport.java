package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

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

    @Column(name = "softskill", nullable = false)
    BigDecimal softskill;
    @Column(name = "avg_exam_mark", nullable = false)
    BigDecimal avg_exam_mark;
    @Column(name = "middle_exam", nullable = false)
    BigDecimal middle_exam;
    @Column(name = "final_exam", nullable = false)
    BigDecimal final_exam;
    @Column(name = "attitude")
    BigDecimal attitude;
    @Column(name = "final_mark")
    BigDecimal final_mark;
}
