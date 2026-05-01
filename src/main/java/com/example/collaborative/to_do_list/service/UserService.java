package com.example.collaborative.to_do_list.service;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.collaborative.to_do_list.adapter.UserAdapter;
import com.example.collaborative.to_do_list.dto.LoginRequest;
import com.example.collaborative.to_do_list.dto.user.UserRequest;
import com.example.collaborative.to_do_list.exception.ConflictException;
import com.example.collaborative.to_do_list.model.User;
import com.example.collaborative.to_do_list.repo.UserRepo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class UserService {

	private final UserRepo userRepo;

	private final JwtService jwtService;

	private final AuthenticationManager authenticationManager;

	@Value("${app.secure-cookie:true}")
	private boolean secureCookie;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

	public UserService(UserRepo userRepo, JwtService jwtService, AuthenticationManager authenticationManager) {
		this.userRepo = userRepo;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
	}

	@Transactional
	public User createUser(UserRequest userRequest) {
		if (userRepo.existsByEmail(userRequest.email())) {
			throw new ConflictException("email already exists");
		}
		if (userRepo.existsByUsername(userRequest.username())) { // Use existsByUsername
																	// here
			throw new ConflictException("username already exists");
		}
		User user = User.builder()
			.username(userRequest.username())
			.email(userRequest.email())
			.passwordHash(passwordEncoder.encode(userRequest.password()))
			.build();
		return userRepo.save(user);
	}

	// Find user by ID
	public User findById(UUID id) throws Exception {
		return userRepo.findById(id).orElseThrow(() -> new Exception("User not found"));
	}

	public ResponseEntity<?> verify(LoginRequest loginRequest, HttpServletResponse response) {

		System.out.println("Attempting auth for: " + loginRequest.getUsernameOrEmail());

		Authentication authentication; // declare outside try

		try {
			authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
			System.out.println("Auth success: " + authentication.getName());
		}
		catch (BadCredentialsException e) {
			System.out.println("Bad credentials");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
		}
		catch (Exception e) {
			System.out.println("Auth failed: " + e.getClass().getName() + " - " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Auth error");
		}

		// now accessible here
		UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();

		String accessToken = jwtService.generateToken(userDetails.getUsername(),
				Map.of("userId", userDetails.getUser().getId()));

		ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
			.httpOnly(true)
			.secure(secureCookie)
			.path("/")
			.maxAge(60 * 60 * 24)
			.sameSite(secureCookie ? "Strict" : "Lax")
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok("Login successful");
	}

}
