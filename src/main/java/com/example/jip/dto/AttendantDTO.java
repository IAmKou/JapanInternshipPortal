package com.example.jip.dto;

import com.example.jip.entity.Attendant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
@Getter
@Setter
public class AttendantDTO {
    private int attendantId;
    private Attendant.Status status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private int studentId;
    private int scheduleId;
    private String studentName;
    private boolean mark;
    private String img;
    private boolean isFinalized;


    public AttendantDTO() {}

    public AttendantDTO(Attendant attendant) {
        this.attendantId = attendant.getId();
        this.status = attendant.getStatus();
        this.date = attendant.getDate();
        this.studentId = attendant.getStudent().getId();
        this.scheduleId = attendant.getSchedule().getId();
        this.img = attendant.getStudent().getImg();
        this.mark = attendant.getStudent().isMark();
        this.studentName = attendant.getStudent().getFullname();
        this.isFinalized = attendant.getIsFinalized();
    }

    public AttendantDTO(int attendantId, Attendant.Status status, Date date, int studentId, int scheduleId, String studentName, boolean mark, String img, boolean isFinalized) {
        this.attendantId = attendantId;
        this.status = status;
        this.date = date;
        this.studentId = studentId;
        this.scheduleId = scheduleId;
        this.studentName = studentName;
        this.mark = mark;
        this.img = img;
        this.isFinalized = isFinalized;
    }

    @Override
    public String toString() {
        return "AttendantDTO {  status=" + status
                + ", date=" + date + ", studentId=" + studentId + "}";
    }
}
