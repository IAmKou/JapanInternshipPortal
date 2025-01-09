package com.example.jip.controller;

import com.example.jip.dto.NotificationDTO;
import com.example.jip.entity.Notification;
import com.example.jip.repository.NotificationRepository;
import com.example.jip.services.NotificationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            // Trả về kết quả dưới dạng JSON
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Notification created successfully!");

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // Nếu xảy ra lỗi, trả về thông báo lỗi dạng JSON
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to create notification: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }


    @GetMapping("/get/{recipientId}")
    public List<NotificationDTO> getAllUserNotifications(@PathVariable int recipientId) {
        List<Notification> notifications = notificationRepository.findNotificationsByRecipient(recipientId);

        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/delete?{id}")
    public boolean deleteNotification(@PathVariable int id) {
        if(notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return true;
        }
        return false;
    }


    @GetMapping("/getAll")
    public List<NotificationDTO> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findNotifications();

        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }



}
