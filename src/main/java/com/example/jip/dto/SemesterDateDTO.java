package com.example.jip.dto;

import java.sql.Date;

public class SemesterDateDTO {
    private Date startTime;
    private Date endTime;

    public SemesterDateDTO(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
