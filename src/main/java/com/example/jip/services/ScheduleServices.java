package com.example.jip.services;

import com.example.jip.dto.ScheduleDTO;
import com.example.jip.entity.Class;
import com.example.jip.entity.Schedule;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.ScheduleRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@Service
public class ScheduleServices {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ClassRepository classRepository;

    private static final Logger logger = LoggerFactory.getLogger(ScheduleServices.class);

    public List<String> importSchedules(MultipartFile file) throws Exception {
        List<String> errors = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next(); // Skip header row

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    processRow(row, errors);
                } catch (Exception e) {
                    logger.error("Error processing row " + (row.getRowNum() + 1), e);
                    errors.add("Error in row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error reading the file", e);
            errors.add("File processing error: " + e.getMessage());
            e.printStackTrace();
        }

        return errors;
    }

    private void processRow(Row row, List<String> errors) {
        if (isRowEmpty(row)) {
            return;
        }

        try {
            // Define a custom date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Check if the first cell contains a numeric date or string date
            LocalDate date;
            if (row.getCell(0).getCellType() == CellType.NUMERIC) {
                date = row.getCell(0).getLocalDateTimeCellValue().toLocalDate();  // Handle numeric dates
            } else if (row.getCell(0).getCellType() == CellType.STRING) {
                // Parse date using the custom format for string cells
                String dateString = row.getCell(0).getStringCellValue();
                date = LocalDate.parse(dateString, formatter);
            } else {
                throw new IllegalArgumentException("Invalid date format in row " + (row.getRowNum() + 1));
            }

            Schedule.dayOfWeek dow = Schedule.dayOfWeek.valueOf(row.getCell(1).getStringCellValue());
            String className = row.getCell(2).getStringCellValue();
            String location = row.getCell(3).getStringCellValue();
            String rawStartTime = row.getCell(4).getStringCellValue();
            String rawEndTime = row.getCell(5).getStringCellValue();
            String description = row.getCell(6).getStringCellValue();
            String event = row.getCell(7).getStringCellValue();

            // Parse date to SQL Date
            Date sqlDate = Date.valueOf(date);

            // Validation: Event null → start_time & end_time cannot be null
            if ((event == null || event.isBlank()) && (rawStartTime == null || rawStartTime.isBlank() || rawEndTime == null || rawEndTime.isBlank())) {
                errors.add("Row " + (row.getRowNum() + 1) + ": Start time and End time are required when event is not specified.");
                return;
            }


            if (event != null && !event.isBlank()) {
                if ((rawStartTime != null && !rawStartTime.isBlank() && (rawEndTime == null || rawEndTime.isBlank()))
                        || (rawEndTime != null && !rawEndTime.isBlank() && (rawStartTime == null || rawStartTime.isBlank()))) {
                    errors.add("Row " + (row.getRowNum() + 1) + ": Both start time and end time must be filled together when event is specified.");
                    return;
                }
            }

            // Create schedule
            Schedule schedule = new Schedule();
            schedule.setDate(sqlDate);
            schedule.setLocation(location);
            schedule.setDay_of_week(dow);
            schedule.setDescription(description);
            schedule.setEvent(event);

            if (location != null && !location.isBlank()) schedule.setLocation(location);
            if (rawStartTime != null && !rawStartTime.isBlank())
                schedule.setStart_time(Time.valueOf(rawStartTime + ":00"));
            if (rawEndTime != null && !rawEndTime.isBlank())
                schedule.setEnd_time(Time.valueOf(rawEndTime + ":00"));

            // Process regular class schedule if available
            if (className != null && !className.isBlank()) {
                try {
                    Class clasz = classRepository.findByName(className)
                            .orElseThrow(() -> new IllegalArgumentException("Class not found: " + className));
                    schedule.setClasz(clasz);
                } catch (Exception e) {
                    errors.add("Row " + (row.getRowNum() + 1) + ": Class not found: " + className);
                    return;
                }
            }

            // Save to database
            scheduleRepository.save(schedule);
        } catch (Exception e) {
            errors.add("Failed to process row: " + (row.getRowNum() + 1) + " due to: " + e.getMessage());
        }
    }


    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    public ScheduleDTO updateSchedule(int id, String className, ScheduleDTO scheduleDTO) {
        // Fetch the schedule by ID or throw an exception if not found
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found with id " + id));

        // Update the class if className is provided
        if (className != null && !className.isBlank()) {
            Class clasz = classRepository.findByName(className)
                    .orElseThrow(() -> new IllegalArgumentException("Class not found: " + className));
            schedule.setClasz(clasz);
        } else {
            throw new IllegalArgumentException("Class cannot be null or empty.");
        }

        schedule.setStart_time(scheduleDTO.getStartTime());
        schedule.setEnd_time(scheduleDTO.getEndTime());

        schedule.setEvent(scheduleDTO.getEvent());
        schedule.setDescription(scheduleDTO.getDescription());

        schedule.setLocation(scheduleDTO.getLocation());

        validateSchedule(schedule);

        scheduleRepository.save(schedule);

        return new ScheduleDTO(schedule);
    }


    private void validateSchedule(Schedule schedule) {
        // If the event is a holiday, start_time, end_time, and location can be null
        if (schedule.getEvent() != null && schedule.getEvent().equalsIgnoreCase("holiday")) {
            // Allow null start_time, end_time, and location for holidays
            return;
        }

        // Validate if start_time and end_time are null only if event or description is provided
        if ((schedule.getStart_time() == null || schedule.getEnd_time() == null) &&
                (schedule.getEvent() == null || schedule.getEvent().isBlank())) {
            throw new IllegalArgumentException("Start time and end time can be null only if event or description is provided.");
        }

        // If event or description is provided, check if location is null
        if ((schedule.getEvent() != null && !schedule.getEvent().isBlank()) &&
                schedule.getLocation() == null) {
            throw new IllegalArgumentException("Location can be null only if event or description is provided.");
        }

        // If start_time and end_time are provided, location should not be null unless event or description is given
        if ((schedule.getStart_time() != null && schedule.getEnd_time() != null) &&
                schedule.getLocation() == null && (schedule.getEvent() == null || schedule.getEvent().isBlank())) {
            throw new IllegalArgumentException("Location cannot be null unless event or description is provided.");
        }
    }
}


