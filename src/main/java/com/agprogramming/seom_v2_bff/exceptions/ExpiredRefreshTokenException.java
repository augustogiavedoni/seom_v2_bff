package com.agprogramming.seom_v2_bff.exceptions;

public class ExpiredRefreshTokenException extends MainException {
	private static final long serialVersionUID = 1L;
	public static final String error = "expired-refresh-token";
	public static final String description = "The refresh token is expired";
	public static final int status = 403;
	

	public ExpiredRefreshTokenException() {
		super(null, error, description, status);
	}
}
