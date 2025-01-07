package com.example.jip.dto;

import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class ScheduleAttendanceDTO {
    private int scheduleId;
    private Date scheduleDate;
    private Schedule.dayOfWeek dayOfWeek;
    private String room;
    private String activity;
    private int studentId;
    private Attendant.Status attendanceStatus;
    private Date attendanceDate;

    public ScheduleAttendanceDTO(int scheduleId, Date scheduleDate, Schedule.dayOfWeek dayOfWeek, String room, String activity
            , int studentId, Attendant.Status attendanceStatus, Date attendanceDate) {
        this.scheduleId = scheduleId;
        this.scheduleDate = scheduleDate;
        this.dayOfWeek = dayOfWeek;
        this.room = room;
        this.activity = activity;
        this.studentId = studentId;
        this.attendanceStatus = attendanceStatus;
        this.attendanceDate = attendanceDate;
    }

}
