package com.khutircraftubackend.ratelimit;

import com.khutircraftubackend.exception.GlobalErrorResponseWriter;
import com.khutircraftubackend.exception.httpStatus.ToManyRequestException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitSecurityService securityService;
    private final GlobalErrorResponseWriter errorWriter;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        
        String ip = request.getRemoteAddr();
        try {
            securityService.checkDdosProtection(ip);
            filterChain.doFilter(request, response);
        } catch (ToManyRequestException e) {
            errorWriter.write(request, response,
                    HttpStatus.TOO_MANY_REQUESTS,
                    e.getMessage(),
                    request.getRequestURI());
        }
    }
}