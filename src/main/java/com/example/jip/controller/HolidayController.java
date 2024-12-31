package com.example.jip.controller;

import com.example.jip.entity.Holiday;
import com.example.jip.services.HolidayServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/holiday")
public class HolidayController {
    @Autowired
    private HolidayServices holidayServices;

    @GetMapping("/get")
    public ResponseEntity<List<Holiday>> getHolidays(
            @RequestParam("startDate") Date startDate,
            @RequestParam("endDate") Date endDate) {
        List<Holiday> holidays = holidayServices.getHolidaysInRange(startDate, endDate);
        return ResponseEntity.ok(holidays);
    }
}
