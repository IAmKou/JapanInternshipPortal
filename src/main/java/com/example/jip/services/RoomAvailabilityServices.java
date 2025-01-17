package com.example.jip.services;

import com.example.jip.dto.RoomAvailabilityDTO;
import com.example.jip.entity.Holiday;
import com.example.jip.entity.Room;
import com.example.jip.entity.RoomAvailability;
import com.example.jip.entity.Semester;
import com.example.jip.repository.HolidayRepository;
import com.example.jip.repository.RoomAvailabilityRepository;
import com.example.jip.repository.RoomRepository;
import com.example.jip.repository.SemesterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@Service
public class RoomAvailabilityServices {

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    public List<RoomAvailabilityDTO> getAvailabilityForDate(Date date) {
        List<RoomAvailability> availabilities = roomAvailabilityRepository.findByDate(date);
        return availabilities.stream().map(availability -> new RoomAvailabilityDTO(
                availability.getRoom().getId(),
                availability.getRoom().getName(),
                availability.getDate(),
                availability.getStatus()
        )).collect(Collectors.toList());
    }

    public void initializeRoomAvailabilityForSemester(int semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new IllegalArgumentException("Semester not found with ID: " + semesterId));

        List<Room> rooms = roomRepository.findAll();
        List<Date> holidays = holidayRepository.findByDateBetween(
                semester.getStart_time(), semester.getEnd_time()
        ).stream().map(Holiday::getDate).toList();

        List<RoomAvailability> availabilityList = new ArrayList<>();

        Date currentDate = semester.getStart_time();
        while (!currentDate.after(semester.getEnd_time())) {
            LocalDate localDate = currentDate.toLocalDate();
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();

            // Skip Saturdays, Sundays, and holidays
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY || holidays.contains(currentDate)) {
                currentDate = Date.valueOf(localDate.plusDays(1));
                continue;
            }

            for (Room room : rooms) {
                // Check if an entry already exists
                if (!roomAvailabilityRepository.existsByRoomAndDate(room, currentDate)) {
                    RoomAvailability availability = new RoomAvailability();
                    availability.setRoom(room);
                    availability.setDate(currentDate);
                    availability.setStatus(RoomAvailability.Status.Available);
                    availabilityList.add(availability);
                }
            }

            // Increment the date by one day
            currentDate = Date.valueOf(localDate.plusDays(1));
        }

        roomAvailabilityRepository.saveAll(availabilityList);
    }

    public void initializeRoomAvailabilityForUpdateSemester(int semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new IllegalArgumentException("Semester not found with ID: " + semesterId));

        List<Room> rooms = roomRepository.findAll();

        List<Date> holidays = holidayRepository.findByDateBetween(
                semester.getStart_time(), semester.getEnd_time()
        ).stream().map(Holiday::getDate).toList();

        // Fetch availability for the specific semester
        List<RoomAvailability> existingAvailability = roomAvailabilityRepository.findBySemesterIdIncludingUnlinked(
                semesterId, semester.getStart_time(), semester.getEnd_time());
        log.info("Existing room availability for semester {}: {}", semesterId, existingAvailability);

        Set<Date> validDates = new HashSet<>();
        Date currentDate = semester.getStart_time();
        while (!currentDate.after(semester.getEnd_time())) {
            LocalDate localDate = currentDate.toLocalDate();
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();

            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY && !holidays.contains(currentDate)) {
                validDates.add(currentDate);
            }

            currentDate = Date.valueOf(localDate.plusDays(1));
        }

        log.info("Valid dates for semester {}: {}", semesterId, validDates);

        // Identify and delete outdated room availability
        List<RoomAvailability> toRemove = existingAvailability.stream()
                .filter(ra -> !validDates.contains(ra.getDate()))
                .toList();
        log.info("RoomAvailabilities to remove: {}", toRemove);

        if (!toRemove.isEmpty()) {
            roomAvailabilityRepository.deleteAll(toRemove);
            log.info("Deleted {} RoomAvailability records for semester {}", toRemove.size(), semesterId);
        } else {
            log.info("No RoomAvailability records to remove for semester {}", semesterId);
        }

        // Add missing entries for all rooms
        List<RoomAvailability> toAdd = new ArrayList<>();
        for (Date validDate : validDates) {
            for (Room room : rooms) {
                if (!roomAvailabilityRepository.existsByRoomAndDate(room, validDate)) {
                    RoomAvailability availability = new RoomAvailability();
                    availability.setRoom(room);
                    availability.setDate(validDate);
                    availability.setStatus(RoomAvailability.Status.Available);
                    toAdd.add(availability);
                }
            }
        }

        log.info("RoomAvailabilities to add: {}", toAdd);
        roomAvailabilityRepository.saveAll(toAdd);
    }


    public void initializeRoomAvailabilityForSemesterAndRoom(int semesterId, Room room) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new IllegalArgumentException("Semester not found with ID: " + semesterId));

        List<Date> holidays = holidayRepository.findByDateBetween(
                semester.getStart_time(), semester.getEnd_time()
        ).stream().map(Holiday::getDate).toList();

        List<RoomAvailability> availabilityList = new ArrayList<>();

        Date currentDate = semester.getStart_time();
        while (!currentDate.after(semester.getEnd_time())) {
            LocalDate localDate = currentDate.toLocalDate();
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();

            // Skip Saturdays, Sundays, and holidays
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY || holidays.contains(currentDate)) {
                currentDate = Date.valueOf(localDate.plusDays(1));
                continue;
            }

            // Check if an entry already exists
            if (!roomAvailabilityRepository.existsByRoomAndDate(room, currentDate)) {
                RoomAvailability availability = new RoomAvailability();
                availability.setRoom(room);
                availability.setDate(currentDate);
                availability.setStatus(RoomAvailability.Status.Available);
                availabilityList.add(availability);
            }

            // Increment the date by one day
            currentDate = Date.valueOf(localDate.plusDays(1));
        }

        roomAvailabilityRepository.saveAll(availabilityList);
    }

    public List<RoomAvailabilityDTO> getRoomAvailability(String roomName, Date date, RoomAvailability.Status status) {
        return roomAvailabilityRepository.findRoomAvailability(roomName, date, status);
    }
}