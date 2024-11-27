package com.example.jip.repository;

import com.example.jip.entity.Notification;
import com.example.jip.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
