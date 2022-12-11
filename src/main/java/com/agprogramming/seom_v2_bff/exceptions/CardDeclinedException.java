package com.agprogramming.seom_v2_bff.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CardDeclinedException extends MainException{
	private static final long serialVersionUID = 1L;
	public static final String error = "card-declined";
	public static final String description = "The card was declined by the payment provider service.";
	public static final int status = 400;
	

	public CardDeclinedException(String path) {
		super(path, error, description, status);
	}
}
