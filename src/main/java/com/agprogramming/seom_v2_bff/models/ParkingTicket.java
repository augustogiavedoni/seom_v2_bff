package com.agprogramming.seom_v2_bff.models;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parking_tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingTicket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	private LocalTime startTime;
	
	private LocalTime endTime;
	
	@NotNull
	private LocalDate date;
	
	@NotNull
	private double latitude;
	
	@NotNull
	private double longitude;
	
	@NotBlank
	@Size(max = 20)
	private String licensePlate;
	
	@NotBlank
	@Size(max = 15)
	private String userCuil;
}
