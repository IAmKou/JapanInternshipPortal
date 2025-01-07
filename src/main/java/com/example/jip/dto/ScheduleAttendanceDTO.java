package com.example.jip.dto;

import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class ScheduleAttendanceDTO {
    private Long scheduleId;
    private Date scheduleDate;
    private Schedule.dayOfWeek dayOfWeek;
    private String room;
    private String activity;
    private int studentId;
    private Attendant.Status attendanceStatus;
    private String color;

    public ScheduleAttendanceDTO(
            Long scheduleId,
            Date scheduleDate,
            Schedule.dayOfWeek dayOfWeek,
            String room,
            String activity,
            int studentId,
            Attendant.Status attendanceStatus,
            String color
    ) {
        this.scheduleId = scheduleId;
        this.scheduleDate = scheduleDate;
        this.dayOfWeek = dayOfWeek;
        this.room = room;
        this.activity = activity;
        this.studentId = studentId;
        this.attendanceStatus = attendanceStatus;
        this.color = color;
    }


}
