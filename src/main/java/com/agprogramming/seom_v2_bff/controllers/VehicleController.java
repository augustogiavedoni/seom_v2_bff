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

import com.agprogramming.seom_v2_bff.models.Vehicle;
import com.agprogramming.seom_v2_bff.repository.VehicleRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
	@Autowired
	VehicleRepository vehicleRepository;
	
	@GetMapping()
	public ResponseEntity<?> getVehicles(@Valid @RequestParam String userCuil) {
		List<Vehicle> vehicles = vehicleRepository.findAllByOwnerCuil(userCuil);
		
		return ResponseEntity.ok(vehicles);
	}
}
