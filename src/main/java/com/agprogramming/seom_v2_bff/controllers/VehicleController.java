package com.agprogramming.seom_v2_bff.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agprogramming.seom_v2_bff.exceptions.ParkingTicketNotFoundException;
import com.agprogramming.seom_v2_bff.exceptions.VehicleAlreadyParkedException;
import com.agprogramming.seom_v2_bff.exceptions.VehicleNotFoundException;
import com.agprogramming.seom_v2_bff.models.ParkingTicket;
import com.agprogramming.seom_v2_bff.models.Vehicle;
import com.agprogramming.seom_v2_bff.payloads.request.ChangeParkingStatusRequest;
import com.agprogramming.seom_v2_bff.payloads.request.StartParkingRequest;
import com.agprogramming.seom_v2_bff.repository.ParkingTicketRepository;
import com.agprogramming.seom_v2_bff.repository.VehicleRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
	@Autowired
	VehicleRepository vehicleRepository;

	@Autowired
	ParkingTicketRepository parkingTicketRepository;

	@GetMapping()
	public ResponseEntity<?> getVehicles(@Valid @RequestParam String userCuil) {
		List<Vehicle> vehicles = vehicleRepository.findAllByOwnerCuil(userCuil);

		return ResponseEntity.ok(vehicles);
	}

	@RequestMapping(method = RequestMethod.PATCH, value = "/park")
	public ResponseEntity<?> parkVehicle(@Valid @RequestBody StartParkingRequest request) {
		final boolean isAlreadyParked = parkingTicketRepository
				.findByLicensePlateAndEndTime(request.getLicensePlate(), null).isPresent();

		if (isAlreadyParked) {
			throw new VehicleAlreadyParkedException("/api/vehicles/park");
		}

		Vehicle vehicle = vehicleRepository.findByLicensePlate(request.getLicensePlate())
				.orElseThrow(() -> new VehicleNotFoundException("/api/vehicles/park"));

		vehicle.setParked(true);

		vehicleRepository.save(vehicle);

		ParkingTicket parkingTicket = new ParkingTicket();

		parkingTicket.setLicensePlate(request.getLicensePlate());
		parkingTicket.setLatitude(request.getLatitude());
		parkingTicket.setLongitude(request.getLongitude());
		parkingTicket.setDate(LocalDate.now());
		parkingTicket.setStartTime(LocalTime.now());

		parkingTicketRepository.save(parkingTicket);

		return ResponseEntity.ok(vehicle);
	}

	@RequestMapping(method = RequestMethod.PATCH, value = "/unpark")
	public ResponseEntity<?> unparkVehicle(@Valid @RequestBody ChangeParkingStatusRequest request) {
		ParkingTicket parkingTicket = parkingTicketRepository
				.findByLicensePlateAndEndTime(request.getLicensePlate(), null)
				.orElseThrow(() -> new ParkingTicketNotFoundException("/api/vehicles/park"));

		Vehicle vehicle = vehicleRepository.findByLicensePlate(request.getLicensePlate())
				.orElseThrow(() -> new VehicleNotFoundException("/api/vehicles/park"));

		parkingTicket.setEndTime(LocalTime.now());
		vehicle.setParked(false);

		parkingTicketRepository.save(parkingTicket);
		vehicleRepository.save(vehicle);

		return ResponseEntity.ok(vehicle);
	}
}
