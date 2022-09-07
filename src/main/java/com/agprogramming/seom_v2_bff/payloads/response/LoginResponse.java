package com.agprogramming.seom_v2_bff.payloads.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
	private String token;
	private String refreshToken;
	private Long id;
	private String firstName;
	private String lastName;
	private String cuil;
	private String email;
	private List<String> roles;
	private Date birthdate;
}