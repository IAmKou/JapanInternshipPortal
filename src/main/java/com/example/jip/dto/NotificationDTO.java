package com.example.jip.dto;

import com.example.jip.entity.Notification;

public class NotificationDTO {
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
