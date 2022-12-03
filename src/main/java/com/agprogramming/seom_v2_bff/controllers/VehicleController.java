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
import com.agprogramming.seom_v2_bff.models.Receipt;
import com.agprogramming.seom_v2_bff.models.Vehicle;
import com.agprogramming.seom_v2_bff.payloads.request.ChangeParkingStatusRequest;
import com.agprogramming.seom_v2_bff.payloads.request.StartParkingRequest;
import com.agprogramming.seom_v2_bff.repository.ParkingTicketRepository;
import com.agprogramming.seom_v2_bff.repository.ReceiptRepository;
import com.agprogramming.seom_v2_bff.repository.VehicleRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
	@Autowired
	VehicleRepository vehicleRepository;

	@Autowired
	ParkingTicketRepository parkingTicketRepository;
	
	@Autowired
	ReceiptRepository receiptRepository;
	
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

		parkingTicket.setUserCuil(vehicle.getOwnerCuil());
		parkingTicket.setLicensePlate(request.getLicensePlate());
		parkingTicket.setLatitude(request.getLatitude());
		parkingTicket.setLongitude(request.getLongitude());
		parkingTicket.setDate(LocalDate.now());
		parkingTicket.setStartTime(LocalTime.now());

		parkingTicketRepository.save(parkingTicket);

		return ResponseEntity.ok(vehicle);
	}

	@RequestMapping(method = RequestMethod.PATCH, value = "/unpark")
	public ResponseEntity<Vehicle> unparkVehicle(@Valid @RequestBody ChangeParkingStatusRequest request) {
		ParkingTicket parkingTicket = parkingTicketRepository
				.findByLicensePlateAndEndTime(request.getLicensePlate(), null)
				.orElseThrow(() -> new ParkingTicketNotFoundException("/api/vehicles/unpark"));

		Vehicle vehicle = vehicleRepository.findByLicensePlate(request.getLicensePlate())
				.orElseThrow(() -> new VehicleNotFoundException("/api/vehicles/unpark"));

		vehicle.setParked(false);
		parkingTicket.setEndTime(LocalTime.now());
		
		Receipt receipt = generateReceipt(vehicle, parkingTicket);
		
		parkingTicket.setReceipt(receipt);

		parkingTicketRepository.save(parkingTicket);
		vehicleRepository.save(vehicle);

		return ResponseEntity.ok(vehicle);
	}
	
	private Receipt generateReceipt(Vehicle vehicle, ParkingTicket parkingTicket) {
		Receipt receipt = new Receipt();
		float amount = calculateAmount(vehicle, parkingTicket);
		
		receipt.setDate(parkingTicket.getDate());
		receipt.setAmount(String.valueOf(amount));
		
		if (amount == 0.0) {
			receipt.setPaid(true);
		} else {
			receipt.setPaid(false);
		}
		
		Receipt savedReceipt = receiptRepository.save(receipt);
		
		return savedReceipt;
	}
	
	private float calculateAmount(Vehicle vehicle, ParkingTicket parkingTicket) {
		int accumulatedTimeParked = vehicle.getTimeParked() + parkingTicket.getEndTime().compareTo(parkingTicket.getStartTime());
		float amount = 0.0f;
		
		if (accumulatedTimeParked < 30) {
			amount = 0.0f;
		} else if (accumulatedTimeParked >= 30 && accumulatedTimeParked < 90) {
			int minutesWithSecondRate = accumulatedTimeParked - 29;
			
			amount = minutesWithSecondRate * 0.5f;
		} else if (accumulatedTimeParked >= 90 && accumulatedTimeParked < 150) {
			int minutesWithThirdRate = accumulatedTimeParked - 89;
			
			amount = 59 * 0.5f + minutesWithThirdRate * 0.75f;
		} else {
			int minutesWithFourthRate = accumulatedTimeParked - 149;
			
			amount = 59 * 0.5f + 59 * 0.75f + minutesWithFourthRate * 1.0f;
		}
		
		vehicle.setTimeParked(accumulatedTimeParked);
		
		return amount;
	}
}
