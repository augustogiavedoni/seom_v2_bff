package com.agprogramming.seom_v2_bff.exceptions;

import lombok.Getter;

@Getter
public abstract class MainException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String path;
	private String error;
	private String message;
	private int status;
	
	MainException(String path, String error, String message, int status) {
		super(String.format("MainException with error: %s", error));
		
		this.path = path;
		this.error = error;
		this.message = message;
		this.status = status;
	}
}
