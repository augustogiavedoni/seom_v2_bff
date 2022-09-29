package com.agprogramming.seom_v2_bff.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.agprogramming.seom_v2_bff.exceptions.ParkingTicketNotFoundException;
import com.agprogramming.seom_v2_bff.exceptions.VehicleAlreadyParkedException;
import com.agprogramming.seom_v2_bff.exceptions.VehicleNotFoundException;

@RestControllerAdvice
public class VehicleControllerAdvice {
	@ExceptionHandler(VehicleAlreadyParkedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleNullPointerExceptions(VehicleAlreadyParkedException exception, WebRequest request) {
		return new ErrorResponse(exception.getPath(), exception.getError(),
				exception.getMessage(), exception.getStatus());
	}

	@ExceptionHandler(VehicleNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNullPointerExceptions(VehicleNotFoundException exception) {
		return new ErrorResponse(exception.getPath(), exception.getError(),
				exception.getMessage(), exception.getStatus());
	}

	@ExceptionHandler(ParkingTicketNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNullPointerExceptions(ParkingTicketNotFoundException exception) {
		return new ErrorResponse(exception.getPath(), exception.getError(),
				exception.getMessage(), exception.getStatus());
	}
}
