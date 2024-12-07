package com.khutircraftubackend.address.exception;

import com.khutircraftubackend.address.exception.address.AccessDeniedException;
import com.khutircraftubackend.address.exception.address.AddressNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class AddressExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(AddressNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public AddressErrorResponse handleAddressNotFoundException(
			AddressNotFoundException ex,
			HttpServletRequest request) {
		
        log.error("Address not found: ", ex);
		
		return AddressErrorResponse.builder()
				.status(HttpStatus.NO_CONTENT.value())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase())
				.message(ex.getLocalizedMessage())
				.path(request.getRequestURI())
				.build();
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
    public AddressErrorResponse handleAccessDeniedException(
			AccessDeniedException ex,
			HttpServletRequest request) {
		
		log.error("Access denied: ", ex);
		
        return AddressErrorResponse.builder()
				.status(HttpStatus.FORBIDDEN.value())
				.error(HttpStatus.FORBIDDEN.getReasonPhrase())
				.message(ex.getLocalizedMessage())
				.path(request.getRequestURI())
				.build();
    }
}
