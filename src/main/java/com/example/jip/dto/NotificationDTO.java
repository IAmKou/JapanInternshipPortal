package com.example.jip.dto;

import com.example.jip.entity.Notification;

public class NotificationDTO {
    private String title;
    private String content;
    private int ownerId;

    public NotificationDTO() {
    }

    public NotificationDTO(Notification notification) {
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.ownerId = notification.getAccount().getId();
    }

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

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
