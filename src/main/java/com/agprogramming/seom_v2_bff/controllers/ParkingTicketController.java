package com.agprogramming.seom_v2_bff.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agprogramming.seom_v2_bff.models.ParkingTicket;
import com.agprogramming.seom_v2_bff.repository.ParkingTicketRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/parking-tickets")
public class ParkingTicketController {
	@Autowired
	ParkingTicketRepository parkingTicketRepository;
	
	@GetMapping()
	public ResponseEntity<?> getRecentyActivity(@Valid @RequestParam String userCuil) {
		List<ParkingTicket> parkingTickets = parkingTicketRepository.findByUserCuilAndEndTimeNotNull(userCuil);
		
		return ResponseEntity.ok(parkingTickets);
	}
}
