package com.agprogramming.seom_v2_bff.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
	private String path;
	private String error;
	private String message;
	private int status;
}
