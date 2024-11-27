package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Notification;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Notification createNotification(String title, String content, int ownerId) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }

        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty.");
        }

        Optional<Account> account = accountRepository.findById(ownerId);
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Account not found with ID: " + ownerId);
        }

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setAccount(account.get());

        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
