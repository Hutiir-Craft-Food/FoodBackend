package com.khutircraftubackend.jwtToken;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.khutircraftubackend.globalException.exception.MalformedJwtTokenException;
import com.khutircraftubackend.globalException.exception.UnsupportedJwtTokenException;
import com.khutircraftubackend.auth.UserDetailsImpl;
import com.khutircraftubackend.auth.UserDetailsServicesImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final JWTVerifier jwtVerifier;
    private final UserDetailsServicesImpl userDetailsServices;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Перевіряємо, чи шлях запиту є шляхом для реєстрації
        String path = request.getRequestURI();
        if (path.startsWith("/v1/user/register")) {
            // Якщо це запит на реєстрацію, пропускаємо перевірку токену
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        log.info("Token: {}", authHeader);
        String token = null;

        HttpStatus errorStatus = null;
        String errorMessage = null;
        boolean hasError = false;
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                token = authHeader.substring(7);
                DecodedJWT jwt = jwtVerifier.verify(token);
                String email = jwt.getSubject();
                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsServices.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                log.error("Authorization header is missing or does not start with Bearer");
            }
        } catch (TokenExpiredException e) {
            log.error("Token has expired: {}", e.getMessage());
            errorStatus = HttpStatus.UNAUTHORIZED;
            errorMessage = ("Token has expired. Please re-authenticate");
            hasError = true;
        } catch (MalformedJwtTokenException e) {
            log.error("Invalid JWT token format: {}. Token: {}", e.getMessage(), token);
            errorStatus = HttpStatus.UNAUTHORIZED;
            errorMessage = ("Invalid JWT token format");
            hasError = true;
        } catch (UnsupportedJwtTokenException e) {
            log.error("Unsupported JWT token: {}. Token: {}", e.getMessage(), token);
            errorStatus = HttpStatus.UNAUTHORIZED;
            errorMessage = ("Unsupported JWT token");
            hasError = true;
        } catch (JWTVerificationException e) {
            log.error("JWT Token verification failed: {}", e.getMessage());
            errorStatus = HttpStatus.UNAUTHORIZED;
            errorMessage = ("Invalid JWT Token");
            hasError = true;
        } catch (Exception e) {
            log.error("Cannot set userEntity authentication: {}", e.getMessage());
            errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage = ("Internal Server Error");
            hasError = true;
        } finally {
            if (hasError) {
                response.setStatus(errorStatus.value());
                response.getWriter().write(errorMessage);
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
