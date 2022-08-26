package com.agprogramming.seom_v2_bff.advice;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.agprogramming.seom_v2_bff.exceptions.UnexistingRefreshTokenException;

@RestControllerAdvice
public class TokenControllerAdvice {
	@ExceptionHandler(value = UnexistingRefreshTokenException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessage handleTokenRefreshException(UnexistingRefreshTokenException ex, WebRequest request) {
		return new ErrorMessage(HttpStatus.FORBIDDEN.value(), new Date(), ex.getMessage(),
				request.getDescription(false));
	}
}