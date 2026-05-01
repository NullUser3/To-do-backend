package com.example.collaborative.to_do_list.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.collaborative.to_do_list.adapter.UserAdapter;
import com.example.collaborative.to_do_list.dto.LoginRequest;
import com.example.collaborative.to_do_list.dto.user.UserResponse;
import com.example.collaborative.to_do_list.dto.user.UserResponse.UserResponsePrivate;
import com.example.collaborative.to_do_list.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api")
public class AuthController {

	private final UserService userService;

	@Value("${app.secure-cookie:true}")
	private boolean secureCookie;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
		return userService.verify(loginRequest, response);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie.from("accessToken", "")
			.httpOnly(true)
			.secure(secureCookie)
			.path("/")
			.maxAge(0)
			.sameSite(secureCookie ? "Strict" : "Lax") // Strict in prod, Lax in dev for
														// easier testing
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		return ResponseEntity.ok("Logged out successfully");
	}

	@GetMapping("/auth-user")
	public ResponseEntity<UserResponsePrivate> getAuthUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();
		UserResponsePrivate userResponse = UserResponsePrivate.in(userDetails.getUser());
		return ResponseEntity.ok(userResponse);
	}

}
