package com.agprogramming.seom_v2_bff.payloads.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginRequest {
	@NotBlank
	private String email;

	@NotBlank
	private String password;
}
