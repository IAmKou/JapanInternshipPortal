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
    @Column(name = "mark_rp_id")
    private int mark_rp_id;

    @Column(name = "exam_id")
    private int exam_id;
}
