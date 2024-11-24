package com.example.jip.services;

import com.example.jip.entity.Class;
import com.example.jip.entity.Schedule;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.ScheduleRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.Date;
import java.sql.Time;

@Service
public class ScheduleServices {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ClassRepository classRepository;

    public void importSchedules(InputStream inputStream) throws Exception {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
            Row row = sheet.getRow(i);
            if (row == null) continue;

            // Read and parse cell values
            String rawDate = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : null;
            if (rawDate == null || rawDate.isBlank()) continue; // Skip rows without a date
            Date date = Date.valueOf(rawDate);

            String rawDayOfWeek = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;
            Schedule.dayOfWeek dayOfWeek = rawDayOfWeek != null && !rawDayOfWeek.isBlank()
                    ? Schedule.dayOfWeek.valueOf(rawDayOfWeek)
                    : null;

            String className = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null;
            String location = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : null;
            String rawStartTime = row.getCell(4) != null ? row.getCell(4).getStringCellValue() : null;
            String rawEndTime = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : null;
            String description = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null;
            String event = row.getCell(7) != null ? row.getCell(7).getStringCellValue() : null;

            // Create schedule
            Schedule schedule = new Schedule();
            schedule.setDate(date);
            schedule.setDay_of_week(dayOfWeek);
            schedule.setDescription(description);
            schedule.setEvent(event);

            // Process regular class schedule if available
            if (className != null && !className.isBlank()) {
                Class clasz = classRepository.findByName(className)
                        .orElseThrow(() -> new IllegalArgumentException("Class not found: " + className));
                schedule.setClasz(clasz);
            }

            if (location != null && !location.isBlank()) schedule.setLocation(location);
            if (rawStartTime != null && !rawStartTime.isBlank())
                schedule.setStart_time(Time.valueOf(rawStartTime + ":00"));
            if (rawEndTime != null && !rawEndTime.isBlank())
                schedule.setEnd_time(Time.valueOf(rawEndTime + ":00"));

            // Save to database
            scheduleRepository.save(schedule);
        }
    }
}
