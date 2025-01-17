package com.example.jip.repository;

import com.example.jip.dto.RoomAvailabilityDTO;
import com.example.jip.entity.Room;
import com.example.jip.entity.Class;
import com.example.jip.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Integer> {
    List<RoomAvailability> findByDate(Date date);
    boolean existsByRoomAndDate(Room room, Date date);
    Optional<RoomAvailability> findByRoomAndDate(Room room, Date date);
    @Query("SELECT ra.room FROM RoomAvailability ra WHERE ra.status = :status AND ra.date = :date")
    Optional<Room> findFirstAvailableRoomByStatusAndDate(@Param("status") RoomAvailability.Status status, @Param("date") Date date);
    boolean existsByRoomIdAndStatus(Integer roomId, RoomAvailability.Status status);
    @Query("SELECT new com.example.jip.dto.RoomAvailabilityDTO(r.id, r.name, ra.date, ra.status) " +
            "FROM RoomAvailability ra " +
            "JOIN ra.room r " +
            "WHERE (:roomName IS NULL OR r.name LIKE %:roomName%) " +
            "AND (:date IS NULL OR ra.date = :date) " +
            "AND (:status IS NULL OR ra.status = :status)")
    List<RoomAvailabilityDTO> findRoomAvailability(@Param("roomName") String roomName,
                                                   @Param("date") Date date,
                                                   @Param("status") RoomAvailability.Status status);
    @Query("""
       SELECT ra
       FROM RoomAvailability ra
       LEFT JOIN ra.schedule s
       WHERE (s.semester.id = :semesterId OR ra.schedule IS NULL)
       AND ra.date BETWEEN :startDate AND :endDate
       """)
    List<RoomAvailability> findBySemesterIdIncludingUnlinked(
            @Param("semesterId") int semesterId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );





}
