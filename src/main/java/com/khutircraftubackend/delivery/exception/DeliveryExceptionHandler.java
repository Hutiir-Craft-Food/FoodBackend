package com.khutircraftubackend.delivery.exception;

import com.khutircraftubackend.delivery.exception.delivery.DeliveryMethodNotFoundException;
import com.khutircraftubackend.delivery.exception.delivery.InvalidDeliveryArgumentException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class DeliveryExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(DeliveryMethodNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public DeliveryMethodErrorResponse handleDeliveryMethodNotFoundException(
			DeliveryMethodNotFoundException ex,
			HttpServletRequest request) {
		
		log.error("Error occurred: ", ex);
		
		return DeliveryMethodErrorResponse.builder()
				.status(HttpStatus.NOT_FOUND.value())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase())
				.message(ex.getLocalizedMessage())
				.path(request.getRequestURI())
				.build();
	}

	
	@ExceptionHandler(InvalidDeliveryArgumentException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public DeliveryMethodErrorResponse handleInvalidDeliveryArgumentException(
			InvalidDeliveryArgumentException ex,
			HttpServletRequest request) {
		
		log.error("Invalid operation for sellerId: {}, methodId: {}",
				ex.getSellerId(), ex.getMethodId());
		
		return DeliveryMethodErrorResponse.builder()
				.status(HttpStatus.FORBIDDEN.value())
				.error(HttpStatus.FORBIDDEN.getReasonPhrase())
				.message(ex.getLocalizedMessage())
				.path(request.getRequestURI())
				.build();
	}
}
