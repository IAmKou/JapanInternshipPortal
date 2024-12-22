package com.example.jip.controller;

import com.example.jip.dto.NotificationDTO;
import com.example.jip.entity.Notification;
import com.example.jip.repository.NotificationRepository;
import com.example.jip.services.NotificationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationServices notificationServices;

    @Autowired
    private NotificationRepository notificationRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createNotification(@RequestParam("title") String title,
                                           @RequestParam("userId") int senderId) {
        try {
            Integer recipientId = null;
            notificationServices.createNotification(title, senderId);

            return ResponseEntity.ok().body("Notification created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create notification: " + e.getMessage());
        }
    }

    @GetMapping("/get/{recipientId}")
    public List<NotificationDTO> getAllUserNotifications(@PathVariable int recipientId) {
        List<Notification> notifications = notificationRepository.findNotificationsByRecipient(recipientId);

        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }
    @PostMapping("/test-notification")
    public ResponseEntity<String> testNotification() {
        notificationServices.createAutoNotificationForAssignment("Test notification", 1, 71);
        return ResponseEntity.ok("Notification created successfully!");
    }

    @GetMapping("/getAll")
    public List<NotificationDTO> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findNotifications();

        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }



}
