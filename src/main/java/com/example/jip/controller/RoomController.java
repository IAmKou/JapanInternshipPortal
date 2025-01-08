package com.example.jip.controller;

import com.example.jip.dto.RoomAvailabilityDTO;
import com.example.jip.entity.Room;
import com.example.jip.entity.RoomAvailability;
import com.example.jip.entity.Semester;
import com.example.jip.repository.RoomAvailabilityRepository;
import com.example.jip.repository.RoomRepository;
import com.example.jip.repository.SemesterRepository;
import com.example.jip.services.RoomAvailabilityServices;
import com.example.jip.services.RoomServices;
import com.example.jip.services.SemesterServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomAvailabilityServices roomAvailabilityServices;

    @Autowired
    private RoomServices roomServices;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;



    @GetMapping("/room-availability")
    public ResponseEntity<List<RoomAvailabilityDTO>>getRoomAvailability(@RequestParam Date date) {
        List<RoomAvailabilityDTO> availability = roomAvailabilityServices.getAvailabilityForDate(date);
        return ResponseEntity.ok(availability);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody Room room) {

        if (roomRepository.existsByName(room.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Room with the same name already exists.");
        }


        Room createdRoom = roomServices.createRoom(room);


        Date creationDate = new Date(System.currentTimeMillis());


        List<Semester> activeSemesters = semesterRepository.findSemestersEndingAfter(creationDate);
        for (Semester semester : activeSemesters) {
                roomAvailabilityServices.initializeRoomAvailabilityForSemesterAndRoom(semester.getId(), createdRoom);
        }

        return ResponseEntity.ok(createdRoom);
    }

    @GetMapping("/get")
    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRoom(@RequestParam int id) {
        if (!roomRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found.");
        }

        boolean hasOccupiedStatus = roomAvailabilityRepository.existsByRoomIdAndStatus(id, RoomAvailability.Status.Occupied);
        if (hasOccupiedStatus) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Room cannot be deleted as it is occupied on some days.");
        }

        // Proceed to delete the room
        roomRepository.deleteById(id);
        return ResponseEntity.ok("Room deleted successfully.");
    }


    @PutMapping("/update/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable int roomId, @RequestParam String roomName) {
        try {
            roomName = roomName.trim();
            if (roomName.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Room name cannot be empty."));
            }
            Room updatedRoom = roomServices.updateRoom(roomId, roomName);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Room updated successfully!", "room", updatedRoom));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "An unexpected error occurred."));
        }
    }
}
