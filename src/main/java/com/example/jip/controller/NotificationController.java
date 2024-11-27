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
