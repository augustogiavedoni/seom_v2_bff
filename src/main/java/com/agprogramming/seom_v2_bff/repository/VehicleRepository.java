package com.agprogramming.seom_v2_bff.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agprogramming.seom_v2_bff.models.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
	Optional<Vehicle> findByLicensePlate(String licensePlate);
	List<Vehicle> findAllByOwnerCuil(String ownerCuil);
}
