package com.example.jip.dto;

import com.example.jip.entity.Semester;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SemesterDTO {
    private int semesterId;
    private String semesterName;
    private Date semesterStart;
    private Date semesterEnd;

    public SemesterDTO(Semester semester) {
        this.semesterId = semester.getId();
        this.semesterName = semester.getName();
        this.semesterStart = semester.getStart_time();
        this.semesterEnd = semester.getEnd_time();
    }
}
