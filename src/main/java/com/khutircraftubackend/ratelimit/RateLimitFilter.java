package com.khutircraftubackend.ratelimit;

import com.khutircraftubackend.exception.GlobalErrorResponseWriter;
import com.khutircraftubackend.exception.httpStatus.ForbiddenException;
import com.khutircraftubackend.exception.httpStatus.ToManyRequestException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final SecurityService securityService;
    private final GlobalErrorResponseWriter errorWriter;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        try {
            securityService.validateUserAgentIsAllowed(userAgent);
            securityService.checkDdosProtection(ip);
            filterChain.doFilter(request, response);
        } catch (ToManyRequestException e) {
            log.warn("ToManyRequest по IP. Блокировка IP {}, method: {}, user-agent: {}, contentType: {}, message: {}",
                    request.getRemoteAddr(), request.getMethod(), userAgent, response.getContentType(), e.getMessage());
            errorWriter.write(request, response,
                    HttpStatus.TOO_MANY_REQUESTS,
                    e.getMessage(),
                    request.getRequestURI());
        } catch (ForbiddenException e){
            log.warn("Blocked request with User-Agent: {}, IP:{}", userAgent, ip);
            errorWriter.write(request, response,
                    HttpStatus.FORBIDDEN,
                    e.getMessage(),
                    request.getRequestURI());
        }
    }
}