package com.example.jip.repository;


import com.example.jip.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {
Optional<Room> findByName(String name);
}
