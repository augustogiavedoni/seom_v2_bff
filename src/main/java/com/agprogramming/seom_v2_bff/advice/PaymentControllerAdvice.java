package com.agprogramming.seom_v2_bff.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.agprogramming.seom_v2_bff.exceptions.CardDeclinedException;

@RestControllerAdvice
public class PaymentControllerAdvice {
	@ExceptionHandler(value = CardDeclinedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleCardDeclinedException(CardDeclinedException exception, WebRequest request) {
		return new ErrorResponse(exception.getPath(), exception.getError(), exception.getMessage(),
				exception.getStatus());
	}
}