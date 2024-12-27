package com.example.jip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassScheduleDTO {
    private int classId;
    private String className;
    private String room;
    private String semesterName;
}
