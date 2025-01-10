package com.example.jip.dto;

import com.example.jip.entity.RoomAvailability;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
public class RoomAvailabilityDTO {
    private int roomId;
    private String roomName;
    private Date date;
    private RoomAvailability.Status status;
}
