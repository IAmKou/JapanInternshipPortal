package com.example.jip.repository;

import com.example.jip.entity.Room;
import com.example.jip.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Integer> {
    List<RoomAvailability> findByDate(Date date);
    boolean existsByRoomAndDate(Room room, Date date);
    Optional<RoomAvailability> findByRoomAndDate(Room room, Date date);
}
