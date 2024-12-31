package com.example.jip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MarkReportExamId implements Serializable {

    @Column(name = "mark_report_id") // Matches the database schema
    private int markReportId;

    @Column(name = "exam_id") // Matches the database schema
    private int examId;
}
