package com.example.jip.controller;

import com.example.jip.dto.NotificationDTO;
import com.example.jip.entity.Notification;
import com.example.jip.services.NotificationServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationServices notificationServices;

    public NotificationController(NotificationServices notificationServices) {
        this.notificationServices = notificationServices;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createNotification(@RequestBody NotificationDTO notificationDTO) {
        try {
            notificationServices.createNotification(
                    notificationDTO.getTitle(),
                    notificationDTO.getContent(),
                    notificationDTO.getOwnerId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("Notification created");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create notification");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        List<NotificationDTO> notificationDTOList = notificationServices.getAllNotifications()
                .stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());

        if (notificationDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(notificationDTOList);
    }

}
