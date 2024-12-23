package com.example.jip.services;

import com.example.jip.dto.NotificationDTO;
import com.example.jip.entity.*;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Service
public class NotificationServices {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AccountRepository accountRepository;

    public NotificationDTO createNotification(String title, Integer senderId) {
        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));


        Date currentDateTime = new Date();

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setCreatedAt(currentDateTime);
        notification.setAccount(sender);

        notificationRepository.save(notification);

        return new NotificationDTO(notification);
    }

    public void createAutoNotificationForAssignment(String title, Integer senderId, Integer receiverId) {
    Account sender = accountRepository.findById(senderId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));

    Account receiver = accountRepository.findById(receiverId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID"));

    Date currentDateTime = new Date();
    Notification notification = new Notification();
    notification.setTitle(title);
    notification.setCreatedAt(currentDateTime);
    notification.setAccount(sender);
    notification.setRecipientAccount(receiver);

    notificationRepository.save(notification);

    }

}

