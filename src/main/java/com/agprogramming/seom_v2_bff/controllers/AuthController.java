package com.agprogramming.seom_v2_bff.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.agprogramming.seom_v2_bff.payloads.response.LoginResponse;
import com.agprogramming.seom_v2_bff.payloads.response.MessageResponse;
import com.agprogramming.seom_v2_bff.payloads.response.TokenRefreshResponse;
import com.agprogramming.seom_v2_bff.repository.CitizenRepository;
import com.agprogramming.seom_v2_bff.repository.RoleRepository;
import com.agprogramming.seom_v2_bff.repository.UserRepository;
import com.agprogramming.seom_v2_bff.security.jwt.JwtUtils;
import com.agprogramming.seom_v2_bff.security.services.RefreshTokenService;
import com.agprogramming.seom_v2_bff.security.services.UserDetailsImpl;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

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
	@Value("${seom_auth.app.stripeApiKey}")
	private String stripeApiKey;

	@PostMapping("/sign-in")
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
				roles, userDetails.getBirthdate(), userDetails.getStripeId()));
	}

	@PostMapping("/sign-up")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws StripeException {
		if (userRepository.existsByCuil(signUpRequest.getCuil())) {
			throw new CuilAlreadyRegisteredException("/auth/signup");
		} else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new EmailAlreadyInUseException("/auth/signup");
		}

		Citizen citizen = citizenRepository.findByCuil(signUpRequest.getCuil())
				.orElseThrow(() -> new CitizenNotFoundException("/auth/signup"));

		// Create new user's account
		User user = new User(signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()),
				citizen.getFirstName(), citizen.getLastName(), citizen.getBirthdate(), citizen.getCuil());
		
		createStripeCustomer(user);
		
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

	private void createStripeCustomer(User user) throws StripeException {
		Stripe.apiKey = stripeApiKey;
		final String[] preferredLocales = {"es"};
		final String fullName = user.getFirstName().concat(" ").concat(user.getLastName());

		Map<String, Object> params = new HashMap<>();

		params.put("email", user.getEmail());
		params.put("name", fullName);
		params.put("tax_exempt", "none");
		params.put("preferred_locales", preferredLocales);
		
		Customer customer = Customer.create(params);
		
		user.setStripeId(customer.getId());
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

	@PostMapping("/sign-out")
	public ResponseEntity<?> logoutUser(@Valid @RequestBody LogoutRequest logOutRequest) {
		refreshTokenService.deleteByUserId(logOutRequest.getUserId());
		return ResponseEntity.ok(new MessageResponse("Log out successful!"));
	}
}
