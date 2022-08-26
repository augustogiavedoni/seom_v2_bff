package com.agprogramming.seom_v2_bff.payloads.request;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.*;

import lombok.Data;

@Data
public class SignupRequest {
	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	private Set<String> role;

	@NotBlank
	@Size(min = 6, max = 40)
	private String password;
	
	@NotBlank
	@Size(max = 50)
	private String firstName;
	
	@NotBlank
	@Size(max = 50)
	private String lastName;
	
	@NotNull
	private Date birthdate;
	
	@NotBlank
	@Size(max = 15)
	private String cuil;
}
