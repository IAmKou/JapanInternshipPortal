package com.example.jip.services;

import com.example.jip.entity.Room;
import com.example.jip.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServices {

    @Autowired
    private RoomRepository roomRepository;

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room updateRoom(int roomId, String roomName) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));

        if (roomRepository.existsByName(roomName)) {
            throw new IllegalArgumentException("Room name already exists: " + roomName);
        }

        room.setName(roomName);
        return roomRepository.save(room);
    }
}
