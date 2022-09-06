package com.agprogramming.seom_v2_bff.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CuilAlreadyRegisteredException extends MainException{
	private static final long serialVersionUID = 1L;
	public static final String error = "cuil-already-registered";
	public static final String description = "The CUIL is already registered";
	public static final int status = 400;
	

	public CuilAlreadyRegisteredException(String path) {
		super(path, error, description, status);
	}
}
