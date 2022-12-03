package com.agprogramming.seom_v2_bff.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends MainException {
	private static final long serialVersionUID = 1L;
	public static final String error = "user-not-found";
	public static final String description = "The user is not present in the database";
	public static final int status = 404;
	

	public UserNotFoundException(String path) {
		super(path, error, description, status);
	}
}
