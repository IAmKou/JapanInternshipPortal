package com.example.jip.controller;

import com.example.jip.dto.RoomAvailabilityDTO;
import com.example.jip.services.RoomAvailabilityServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomAvailabilityServices roomAvailabilityServices;

    @GetMapping("/room-availability")
    public ResponseEntity<List<RoomAvailabilityDTO>>getRoomAvailability(@RequestParam Date date) {
        List<RoomAvailabilityDTO> availability = roomAvailabilityServices.getAvailabilityForDate(date);
        return ResponseEntity.ok(availability);
    }
}
