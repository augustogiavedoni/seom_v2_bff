package com.agprogramming.seom_v2_bff.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.agprogramming.seom_v2_bff.exceptions.CitizenNotFoundException;
import com.agprogramming.seom_v2_bff.exceptions.CuilAlreadyRegisteredException;
import com.agprogramming.seom_v2_bff.exceptions.EmailAlreadyInUseException;
import com.agprogramming.seom_v2_bff.exceptions.UnexistingRefreshTokenException;

@RestControllerAdvice
public class AuthControllerAdvice {
	@ExceptionHandler(CitizenNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNullPointerExceptions(CitizenNotFoundException exception, WebRequest request) {
		return new ErrorResponse(exception.getPath(), CitizenNotFoundException.error, CitizenNotFoundException.description,
				CitizenNotFoundException.status);
	}

	@ExceptionHandler(CuilAlreadyRegisteredException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleNullPointerExceptions(CuilAlreadyRegisteredException exception, WebRequest request) {
		return new ErrorResponse(exception.getPath(), exception.getError(), exception.getMessage(),
				exception.getStatus());
	}

	@ExceptionHandler(EmailAlreadyInUseException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleNullPointerExceptions(EmailAlreadyInUseException exception, WebRequest request) {
		return new ErrorResponse(exception.getPath(), exception.getError(), exception.getMessage(),
				exception.getStatus());
	}

	@ExceptionHandler(UnexistingRefreshTokenException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleNullPointerExceptions(UnexistingRefreshTokenException exception, WebRequest request) {
		return new ErrorResponse(exception.getPath(), exception.getError(), exception.getMessage(),
				exception.getStatus());
	}
}
