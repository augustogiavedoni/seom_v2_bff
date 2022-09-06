package com.agprogramming.seom_v2_bff.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agprogramming.seom_v2_bff.exceptions.UnexistingRefreshTokenException;
import com.agprogramming.seom_v2_bff.interceptors.SeomClientInterceptor;
import com.agprogramming.seom_v2_bff.repository.RefreshTokenRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
	@Autowired
	RefreshTokenRepository refreshTokenRepository;

	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public String userAccess() throws Exception {
		String token = SeomClientInterceptor.getBearerTokenHeader().split(" ")[1];

		refreshTokenRepository.findByToken(token)
				.orElseThrow(() -> new UnexistingRefreshTokenException("api/test/user"));

		return "User Content.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}
}
