package com.example.jip.services;

import com.example.jip.entity.Curriculum;
import com.example.jip.entity.Holiday;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Semester;
import com.example.jip.repository.CurriculumRepository;
import com.example.jip.repository.HolidayRepository;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SemesterServices {

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private CurriculumRepository curriculumRepository;

    public void saveSemester(Semester semester) {
        semesterRepository.save(semester);
    }

    public void addHolidaysToSemester(List<Holiday> holidays) {
        for (Holiday holiday : holidays) {
            Optional<Holiday> existingHoliday = holidayRepository.findByDate(holiday.getDate());

            if (!existingHoliday.isPresent()) {
                holidayRepository.save(holiday); // Save only if it doesn't already exist
            }
        }
    }


    public void addHolidaysToSchedule(Semester semester, List<Holiday> holidays) {
        for (Holiday holiday : holidays) {
            // Check if the holiday is already added to the schedule
            Optional<Schedule> existingSchedule = scheduleRepository.findByDateAndSemester(holiday.getDate(), semester);
            if (!existingSchedule.isPresent()) {
                Schedule schedule = new Schedule();
                schedule.setDate(holiday.getDate());
                schedule.setActivity(holiday.getName());
                schedule.setColor("#fac702");
                schedule.setStatus(Schedule.status.Draft);


                schedule.setSemester(semester);

                // Optionally set the day_of_week based on the date
                schedule.setDay_of_week(getDayOfWeekFromDate(holiday.getDate()));


                // Save the schedule to the database
                scheduleRepository.save(schedule);
            }
        }
    }
    public boolean isSemesterNameExist(String name) {
        return semesterRepository.existsByName(name);
    }

    public boolean isTimeOverlapping(Date startTime, Date endTime, int currentSemesterId) {
        List<Semester> semesters = semesterRepository.findAll();

        for (Semester semester : semesters) {
            if (semester.getId() == currentSemesterId) {
                continue; // Skip the current semester being updated
            }

            // Check for any overlap
            if (!(startTime.after(semester.getEnd_time()) || endTime.before(semester.getStart_time()))) {
                return true; // Overlap detected
            }
        }
        return false; // No overlap
    }

    public boolean isTimeAdjacent(Date startTime, Date endTime) {
        // Check if any semester's end_time matches the given start_time
        boolean hasEndMatchingStart = semesterRepository.existsByEndTime(startTime);

        // Check if any semester's start_time matches the given end_time
        boolean hasStartMatchingEnd = semesterRepository.existsByStartTime(endTime);

        // Return true if either condition is satisfied
        return hasEndMatchingStart || hasStartMatchingEnd;
    }



    public boolean isTimeOverlap(Date startTime, Date endTime) {
        List<Semester> semesters = semesterRepository.findAll();

        for (Semester semester : semesters) {
            if (!(startTime.after(semester.getEnd_time()) || endTime.before(semester.getStart_time()))) {
                return true; // Overlap detected
            }
        }
        return false;
    }







    private Schedule.dayOfWeek getDayOfWeekFromDate(java.sql.Date date) {
        java.time.LocalDate localDate = date.toLocalDate();
        java.time.DayOfWeek dayOfWeek = localDate.getDayOfWeek();

        switch (dayOfWeek) {
            case MONDAY:
                return Schedule.dayOfWeek.Monday;
            case TUESDAY:
                return Schedule.dayOfWeek.Tuesday;
            case WEDNESDAY:
                return Schedule.dayOfWeek.Wednesday;
            case THURSDAY:
                return Schedule.dayOfWeek.Thursday;
            case FRIDAY:
                return Schedule.dayOfWeek.Friday;
            case SATURDAY:
                return Schedule.dayOfWeek.Saturday;
            case SUNDAY:
                return Schedule.dayOfWeek.Sunday;
            default:
                return null; // In case of an invalid date, you can return null or handle it differently
        }
    }
}

