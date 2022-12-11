package com.agprogramming.seom_v2_bff.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agprogramming.seom_v2_bff.models.ParkingTicket;

public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, Long>{
	Optional<ParkingTicket> findByLicensePlateAndEndTime(String licensePlate, LocalTime endTime);
	List<ParkingTicket> findByUserCuilAndEndTimeNotNull(String userCuil);
}
