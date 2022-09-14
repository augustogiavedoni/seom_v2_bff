package com.agprogramming.seom_v2_bff.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agprogramming.seom_v2_bff.models.Citizen;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {
	Optional<Citizen> findByCuil(String cuil);
	Boolean existsByCuil(String cuil);
}
