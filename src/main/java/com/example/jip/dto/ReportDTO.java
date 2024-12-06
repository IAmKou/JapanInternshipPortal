package com.example.jip.dto;

import com.example.jip.entity.Account;
import com.example.jip.entity.Report;

public class ReportDTO {
    private String title;
    private String content;
    private int reporter_id;



    public ReportDTO(Report report) {
        this.title = report.getTitle();
        this.content = report.getContent();
        this.reporter_id = report.getAccount().getId();
    }

    public ReportDTO() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReporter_id() {
        return reporter_id;
    }

    public void setReporter_id(int reporter_id) {
        this.reporter_id = reporter_id;
    }

}
