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
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleServices {
//
//    @Autowired
//    private ScheduleRepository scheduleRepository;
//
//    @Autowired
//    private ClassRepository classRepository;
//
//    public List<String> importSchedules(InputStream inputStream) throws Exception {
//        List<String> errors = new ArrayList<>();
//        Workbook workbook = WorkbookFactory.create(inputStream);
//        Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
//
//        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
//            Row row = sheet.getRow(i);
//
//            // Ignore empty rows
//            if (row == null || isRowEmpty(row)) {
//                continue;
//            }
//
//            try {
//                // Parse cell values
//                String rawDate = getCellValueAsString(row.getCell(0));
//                if (rawDate == null || rawDate.isBlank()) {
//                    errors.add("Row " + (i + 1) + ": Date is missing.");
//                    continue;
//                }
//                Date date = Date.valueOf(rawDate);
//
//                String rawDayOfWeek = getCellValueAsString(row.getCell(1));
//                Schedule.dayOfWeek dayOfWeek = rawDayOfWeek != null && !rawDayOfWeek.isBlank()
//                        ? Schedule.dayOfWeek.valueOf(rawDayOfWeek)
//                        : null;
//
//                String className = getCellValueAsString(row.getCell(2));
//                String location = getCellValueAsString(row.getCell(3));
//                String rawStartTime = getCellValueAsString(row.getCell(4));
//                String rawEndTime = getCellValueAsString(row.getCell(5));
//                String description = getCellValueAsString(row.getCell(6));
//                String event = getCellValueAsString(row.getCell(7));
//
//                // Create schedule
//                Schedule schedule = new Schedule();
//                schedule.setDate(date);
//                schedule.setDay_of_week(dayOfWeek);
//                schedule.setDescription(description);
//                schedule.setEvent(event);
//
//                // Process regular class schedule if available
//                if (className != null && !className.isBlank()) {
//                    try {
//                        Class clasz = classRepository.findByName(className)
//                                .orElseThrow(() -> new IllegalArgumentException("Class not found: " + className));
//                        schedule.setClasz(clasz);
//                    } catch (Exception e) {
//                        errors.add("Row " + (i + 1) + ": Class not found: " + className);
//                        continue;
//                    }
//                }
//
//                if (location != null && !location.isBlank()) schedule.setLocation(location);
//                if (rawStartTime != null && !rawStartTime.isBlank())
//                    schedule.setStart_time(Time.valueOf(rawStartTime + ":00"));
//                if (rawEndTime != null && !rawEndTime.isBlank())
//                    schedule.setEnd_time(Time.valueOf(rawEndTime + ":00"));
//
//                // Save to database
//                scheduleRepository.save(schedule);
//            } catch (Exception e) {
//                // Add error details for this row
//                errors.add("Row " + (i + 1) + ": " + e.getMessage());
//            }
//        }
//        return errors;
//    }
//
//    private boolean isRowEmpty(Row row) {
//        for (int i = 0; i < row.getLastCellNum(); i++) {
//            Cell cell = row.getCell(i);
//            if (cell != null && cell.getCellType() != CellType.BLANK) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private String getCellValueAsString(Cell cell) {
//        if (cell == null) return null;
//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue().trim();
//            case NUMERIC:
//                if (DateUtil.isCellDateFormatted(cell)) {
//                    return cell.getDateCellValue().toString();
//                } else {
//                    return String.valueOf((int) cell.getNumericCellValue());
//                }
//            case BOOLEAN:
//                return String.valueOf(cell.getBooleanCellValue());
//            case FORMULA:
//                return cell.getCellFormula();
//            default:
//                return null;
//        }
//    }
}


