package com.example.jip.repository;


import com.example.jip.entity.Room;
import com.example.jip.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {
Optional<Room> findByName(String name);
    boolean existsByName(String name);
    @Query(value = "SELECT r.* FROM room r " +
            "JOIN room_availability ra ON r.id = ra.room_id " +
            "WHERE ra.status = :status AND ra.date = :date LIMIT 1",
            nativeQuery = true)
    Optional<Room> findFirstAvailableRoomByStatusAndDate(@Param("status") String status, @Param("date") Date date);
}
