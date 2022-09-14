package com.agprogramming.seom_v2_bff.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agprogramming.seom_v2_bff.exceptions.CitizenNotFoundException;
import com.agprogramming.seom_v2_bff.exceptions.CuilAlreadyRegisteredException;
import com.agprogramming.seom_v2_bff.exceptions.EmailAlreadyInUseException;
import com.agprogramming.seom_v2_bff.exceptions.UnexistingRefreshTokenException;
import com.agprogramming.seom_v2_bff.models.Citizen;
import com.agprogramming.seom_v2_bff.models.RefreshToken;
import com.agprogramming.seom_v2_bff.models.Role;
import com.agprogramming.seom_v2_bff.models.SystemRole;
import com.agprogramming.seom_v2_bff.models.User;
import com.agprogramming.seom_v2_bff.payloads.request.LoginRequest;
import com.agprogramming.seom_v2_bff.payloads.request.LogoutRequest;
import com.agprogramming.seom_v2_bff.payloads.request.SignupRequest;
import com.agprogramming.seom_v2_bff.payloads.request.TokenRefreshRequest;
import com.agprogramming.seom_v2_bff.payloads.response.ErrorResponse;
import com.agprogramming.seom_v2_bff.payloads.response.LoginResponse;
import com.agprogramming.seom_v2_bff.payloads.response.MessageResponse;
import com.agprogramming.seom_v2_bff.payloads.response.TokenRefreshResponse;
import com.agprogramming.seom_v2_bff.repository.CitizenRepository;
import com.agprogramming.seom_v2_bff.repository.RoleRepository;
import com.agprogramming.seom_v2_bff.repository.UserRepository;
import com.agprogramming.seom_v2_bff.security.jwt.JwtUtils;
import com.agprogramming.seom_v2_bff.security.services.RefreshTokenService;
import com.agprogramming.seom_v2_bff.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	CitizenRepository citizenRepository;
	@Autowired
	PasswordEncoder encoder;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	RefreshTokenService refreshTokenService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		String jwt = jwtUtils.generateJwtToken(userDetails);
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
		return ResponseEntity.ok(new LoginResponse(jwt, refreshToken.getToken(), userDetails.getId(),
				userDetails.getFirstName(), userDetails.getLastName(), userDetails.getCuil(), userDetails.getEmail(),
				roles, userDetails.getBirthdate()));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByCuil(signUpRequest.getCuil())) {
			throw new CuilAlreadyRegisteredException("/auth/signup");
		} else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new EmailAlreadyInUseException("/auth/signup");
		}

		Citizen citizen = citizenRepository.findByCuil(signUpRequest.getCuil())
				.orElseThrow(() -> new CitizenNotFoundException("/auth/signup"));

		// Create new user's account
		User user = new User(signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()),
				citizen.getFirstName(), citizen.getLastName(), citizen.getBirthdate(),
				citizen.getCuil());
		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(SystemRole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(SystemRole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);
					break;
				default:
					Role userRole = roleRepository.findByName(SystemRole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return authenticateUser(new LoginRequest(signUpRequest.getEmail(), signUpRequest.getPassword()));
	}

	@PostMapping("/refreshtoken")
	public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
		String requestRefreshToken = request.getRefreshToken();

		return refreshTokenService.findByToken(requestRefreshToken).map(refreshTokenService::verifyExpiration)
				.map(RefreshToken::getUser).map(user -> {
					String token = jwtUtils.generateTokenFromEmail(user.getEmail());
					return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
				}).orElseThrow(() -> new UnexistingRefreshTokenException("/auth/refreshtoken"));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser(@Valid @RequestBody LogoutRequest logOutRequest) {
		refreshTokenService.deleteByUserId(logOutRequest.getUserId());
		return ResponseEntity.ok(new MessageResponse("Log out successful!"));
	}
	
	@ExceptionHandler(CitizenNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNullPointerExceptions(CitizenNotFoundException exception) {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(exception.getPath(), exception.getError(),
				exception.getMessage(), exception.getStatus()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(CuilAlreadyRegisteredException.class)
	public ResponseEntity<ErrorResponse> handleNullPointerExceptions(CuilAlreadyRegisteredException exception) {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(exception.getPath(), exception.getError(),
				exception.getMessage(), exception.getStatus()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmailAlreadyInUseException.class)
	public ResponseEntity<ErrorResponse> handleNullPointerExceptions(EmailAlreadyInUseException exception) {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(exception.getPath(), exception.getError(),
				exception.getMessage(), exception.getStatus()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnexistingRefreshTokenException.class)
	public ResponseEntity<ErrorResponse> handleNullPointerExceptions(UnexistingRefreshTokenException exception) {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(exception.getPath(), exception.getError(),
				exception.getMessage(), exception.getStatus()), HttpStatus.FORBIDDEN);
	}
}
