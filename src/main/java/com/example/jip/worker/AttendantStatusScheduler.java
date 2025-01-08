package com.example.jip.worker;

import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import com.example.jip.repository.AttendantRepository;
import com.example.jip.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class AttendantStatusScheduler {
    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    AttendantRepository attendantRepository;

    @Scheduled(cron = "0 0 17 * * ?") // Runs at 17:00 every day
    public void finalizeAttendance() {
        System.out.println("Starting attendance finalization scheduler at " + LocalDate.now());
        List<Schedule> schedules = scheduleRepository.findAllByDate(LocalDate.now());
        for (Schedule schedule : schedules) {
            System.out.println("Processing schedule ID: " + schedule.getId());
            try {
                List<Attendant> attendants = attendantRepository.findByScheduleId(schedule.getId());
                for (Attendant attendant : attendants) {
                    if (attendant.getStatus() == null) {
                        attendant.setStatus(Attendant.Status.ABSENT);
                    }
                    if (attendant.getEndTime().toLocalTime().isAfter(LocalTime.of(17, 0))) {
                        attendant.setIsFinalized(true);
                    }
                }
                attendantRepository.saveAll(attendants);
                System.out.println("Finalized attendance for schedule ID: " + schedule.getId());
            } catch (Exception e) {
                System.err.println("Error finalizing attendance for schedule ID: " + schedule.getId());
                e.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void finalizeSchedule() {
        System.out.println("Starting schedule finalization scheduler at " + LocalDate.now());

        List<Schedule> schedules = scheduleRepository.findAllByDateLessThanEqual(LocalDate.now());

        for (Schedule schedule : schedules) {
            System.out.println("Processing schedule ID: " + schedule.getId());
            try {
                List<Attendant> attendants = attendantRepository.findByScheduleId(schedule.getId());

                for (Attendant attendant : attendants) {
                    if (!attendant.getIsFinalized()) {
                        attendant.setIsFinalized(true);
                    }
                }

                attendantRepository.saveAll(attendants);
                System.out.println("Finalized attendants for schedule ID: " + schedule.getId());
            } catch (Exception e) {
                System.err.println("Error finalizing schedule ID: " + schedule.getId());
                e.printStackTrace();
            }
        }
    }


}
