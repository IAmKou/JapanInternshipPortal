package com.example.jip.dto;

import com.example.jip.entity.Notification;

public class AutoNotificationDTO {
    private String title;
    private int senderId;
    private int receiverId;
    private String senderName;
    private String receiverName;
    private String className;


    public AutoNotificationDTO(String title, int senderId, int receiverId, String senderName, String receiverName, String className) {
        this.title = title;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.className = className;

    }

    public AutoNotificationDTO() {
    }

    public AutoNotificationDTO(Notification notification) {
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

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
