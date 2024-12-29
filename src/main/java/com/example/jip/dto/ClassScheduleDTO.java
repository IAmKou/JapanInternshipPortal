package com.example.jip.dto;

import com.example.jip.entity.Schedule;
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

    public ClassScheduleDTO(Schedule schedule) {
        this.classId = schedule.getClasz().getId();
        this.className = schedule.getClasz().getName();
        this.room = schedule.getRoom();
        this.semesterName = schedule.getSemester().getName();
    }
}
