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


    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
