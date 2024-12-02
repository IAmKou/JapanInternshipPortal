package com.example.jip.services;

import com.example.jip.dto.NotificationDTO;
import com.example.jip.entity.Account;
import com.example.jip.entity.Notification;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServices {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    public NotificationServices(NotificationRepository notificationRepository, AccountRepository accountRepository) {
        this.notificationRepository = notificationRepository;
        this.accountRepository = accountRepository;
    }

    public Notification createNotification(NotificationDTO notificationDTO) {
        if (notificationDTO.getTitle() == null || notificationDTO.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }

        if (notificationDTO.getContent() == null || notificationDTO.getContent().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty.");
        }

        Optional<Account> account = accountRepository.findById(notificationDTO.getOwnerId());
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Account not found with ID: " + notificationDTO.getOwnerId());
        }

        Notification notification = new Notification();
        notification.setTitle(notificationDTO.getTitle());
        notification.setContent(notificationDTO.getContent());
        notification.setAccount(account.get());

        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsOfAdmin() {
        List<Notification> listAll = notificationRepository.findAll();
        List<Notification> notifications = new ArrayList<>();
        for (Notification notification : listAll) {
            if (notification.getAccount().getRole().getName().equals("ADMIN")) {
                notifications.add(notification);
            }
        }
        return notifications;
    }
}
