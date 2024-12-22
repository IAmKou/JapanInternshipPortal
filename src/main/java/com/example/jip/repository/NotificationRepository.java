package com.example.jip.repository;

import com.example.jip.entity.Notification;
import com.example.jip.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    @Query("SELECT n FROM Notification n WHERE n.recipientAccount.id = :recipientId OR n.recipientAccount IS NULL")
    List<Notification> findNotificationsByRecipient(@Param("recipientId") int recipientId);

    @Query("SELECT n FROM Notification n WHERE n.recipientAccount IS NULL")
    List<Notification> findNotifications();
}
