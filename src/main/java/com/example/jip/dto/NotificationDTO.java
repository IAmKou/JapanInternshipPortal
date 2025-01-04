package com.example.jip.dto;

import com.example.jip.entity.Notification;
import io.opencensus.common.Timestamp;

import java.util.Date;

public class NotificationDTO {
    private Date created;
    private String title;
    private int senderId;

    public NotificationDTO() {
    }

    public NotificationDTO(String title, int senderId) {
        this.title = title;
        this.senderId = senderId;

    }

    public NotificationDTO(Notification notification) {
        this.title = notification.getTitle();
        this.senderId = notification.getAccount().getId();  // Assuming Account has a getId() method
        this.created = notification.getCreatedAt();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

}
