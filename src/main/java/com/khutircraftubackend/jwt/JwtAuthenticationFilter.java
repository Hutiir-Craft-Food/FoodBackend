package com.khutircraftubackend.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.khutircraftubackend.config.UserDetailsConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JWTVerifier jwtVerifier;
	private final UserDetailsConfig userDetailsConfig;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
									@NonNull HttpServletResponse response,
									@NonNull FilterChain filterChain) throws ServletException, IOException {
		
		String authHeader = request.getHeader("Authorization");

		if (StringUtils.startsWith(authHeader, "Bearer ") && authHeader != null) {

			String token = authHeader.substring(7);
			try {
				DecodedJWT jwt = jwtVerifier.verify(token);
				String email = jwt.getSubject();

				UserDetails userDetails = userDetailsConfig.userDetailsService().loadUserByUsername(email);
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			} catch (JWTVerificationException e) {
				log.error(e.getMessage());
			}
		}
		filterChain.doFilter(request, response);
	}
}
