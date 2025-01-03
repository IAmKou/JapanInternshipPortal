package com.example.jip.repository;

import com.example.jip.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {
    Holiday findByDate(LocalDate date);
    Optional<Holiday> findByDate(java.sql.Date date);
    List<Holiday> findByDateBetween(Date startDate, Date endDate);
}
