package com.example.jip.controller;

import com.example.jip.dto.RoomAvailabilityDTO;
import com.example.jip.entity.Room;
import com.example.jip.entity.Semester;
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

}
