package com.agprogramming.seom_v2_bff.models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles", uniqueConstraints = { @UniqueConstraint(columnNames = {"licensePlate" }) })
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotBlank
	@Size(max = 30)
	private String make;
	
	@NotBlank
	@Size(max = 50)
	private String model;
	
	@NotNull
	private int year;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private VehicleType vehicleType;
	
	@NotBlank
	@Size(max = 20)
	private String licensePlate;
	
	@NotBlank
	@Size(max = 15)
	private String ownerCuil;
	
	@NotNull
	private boolean isParked;
	
	@NotNull
	private int timeParked;
}
