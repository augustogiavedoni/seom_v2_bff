package com.agprogramming.seom_v2_bff.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ParkingTicketNotFoundException extends MainException {
	private static final long serialVersionUID = 1L;
	public static final String error = "parking-ticket-not-found";
	public static final String description = "There is no parking ticket present in the database for this vehicle";
	public static final int status = 404;

	public ParkingTicketNotFoundException(String path) {
		super(path, error, description, status);
	}
}
