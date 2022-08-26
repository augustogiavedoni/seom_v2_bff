package com.agprogramming.seom_v2_bff.exceptions;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnexistingRefreshTokenException extends MainException {
	private static final long serialVersionUID = 1L;
	public static final String error = "unexisting-refresh-token";
	public static final String description = "The refresh token is not present in the database";
	public static final int status = 403;
	

	public UnexistingRefreshTokenException(String path) {
		super(path, error, description, status);
	}
}