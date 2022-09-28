package com.agprogramming.seom_v2_bff.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VehicleAlreadyParkedException extends MainException {
	private static final long serialVersionUID = 1L;
	public static final String error = "vehicle-already-parked";
	public static final String description = "The vehicle is already parked";
	public static final int status = 400;
	

	public VehicleAlreadyParkedException(String path) {
		super(path, error, description, status);
	}
}
