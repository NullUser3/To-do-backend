package com.example.collaborative.to_do_list.config;

import com.example.collaborative.to_do_list.service.JwtService;
import com.example.collaborative.to_do_list.service.MyUserDetailsService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

@Component // Marks this class as a Spring-managed bean
@RequiredArgsConstructor // Generates a constructor with required (final) fields
public class JwtAuthenticationFilter extends OncePerRequestFilter {

// Filter that runs
																	// once per request

	// Injecting JwtService to generate/validate JWT tokens
	private final JwtService jwtService;

	// Injecting custom UserDetailsService to load user data from DB
	private final MyUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String jwt = null;

		// 🍪 extract token from HttpOnly cookie
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("accessToken".equals(cookie.getName())) {
					jwt = cookie.getValue();
					break;
				}
			}
		}

		// if no token → continue request
		if (jwt == null) {
			filterChain.doFilter(request, response);
			return;
		}

		String username;

		try {
			username = jwtService.extractUsername(jwt);
		}
		catch (JwtException e) {
			filterChain.doFilter(request, response);
			return;
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			var userDetails = userDetailsService.loadUserByUsername(username);

			if (jwtService.isTokenValid(jwt)) {

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		filterChain.doFilter(request, response);
	}

}
