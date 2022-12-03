package com.agprogramming.seom_v2_bff.payloads.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartParkingRequest {
	@NotBlank
	String licensePlate;
	
	@NotNull
	float latitude;
	
	@NotNull
	float longitude;
}
