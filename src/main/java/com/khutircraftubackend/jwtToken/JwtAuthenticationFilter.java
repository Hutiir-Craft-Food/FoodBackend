package com.khutircraftubackend.jwtToken;

import com.khutircraftubackend.auth.UserDetailsImpl;
import com.khutircraftubackend.auth.UserDetailsServicesImpl;
import com.khutircraftubackend.auth.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsServicesImpl userDetailsServices;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Перевіряємо, чи шлях запиту є шляхом для реєстрації
        String path = request.getRequestURI();
        if (path.startsWith("/v1/user/register")) {
            // Якщо це запит на реєстрацію, просто пропускаємо перевірку токену
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");
            log.info("Header: {}", authHeader);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtUtils.validateJwtToken(token)) {
                    String email = jwtUtils.getJwtEmailFromToken(token);
                    log.info("Extracted email: {}", email);
                        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsServices.loadUserByUsername(email);
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        log.error("Invalid JWT Token: token does not match the stored token for the user");
                    }
                } else {
                    log.error("Invalid JWT Token: validation failed");
                }

        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
            filterChain.doFilter(request, response);
        }
    }
