package com.example.jip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomAvailabilityDTO {
    private Integer roomId;
    private String roomName;
    private String status;
}
