package com.agprogramming.seom_v2_bff.payloads.request;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
	@NotBlank
	private String email;

	@NotBlank
	private String password;
}
