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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                availability.getStatus().name()
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
}
