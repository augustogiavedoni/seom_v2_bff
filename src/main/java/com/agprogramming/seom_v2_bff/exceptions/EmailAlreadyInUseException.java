package com.agprogramming.seom_v2_bff.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class EmailAlreadyInUseException extends MainException {
	private static final long serialVersionUID = 1L;
	public static final String error = "email-already-in-use";
	public static final String description = "The email is already in use";
	public static final int status = 401;
	

	public EmailAlreadyInUseException(String path) {
		super(path, error, description, status);
	}
}
