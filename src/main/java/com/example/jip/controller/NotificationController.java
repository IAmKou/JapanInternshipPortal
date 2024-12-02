package com.example.jip.controller;

import com.example.jip.dto.NotificationDTO;
import com.example.jip.entity.Notification;
import com.example.jip.services.NotificationServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
    public RedirectView createNotification(@RequestParam("title") String title,
                                           @RequestParam("content") String content,
                                           @RequestParam("userId") Integer ownerId) {
        try {
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setTitle(title);
            notificationDTO.setContent(content);
            notificationDTO.setOwnerId(ownerId);

            Notification notification = notificationServices.createNotification(notificationDTO);

            return new RedirectView("/notice-board.html");
        } catch (Exception e) {
            return new RedirectView("/notice-board.html");
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
