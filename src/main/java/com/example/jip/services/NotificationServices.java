package com.example.jip.services;

import com.example.jip.dto.AutoNotificationDTO;
import com.example.jip.dto.NotificationDTO;
import com.example.jip.entity.Account;
import com.example.jip.entity.Notification;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    public AutoNotificationDTO createAutoNotificationForAssignment(String title, Integer senderId, Integer receiverId) {
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

    return new AutoNotificationDTO(notification);
    }


}

