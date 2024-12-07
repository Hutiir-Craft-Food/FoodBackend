package com.khutircraftubackend.seller.qualityCertificates.exception;

import com.khutircraftubackend.seller.qualityCertificates.exception.qualityException.InvalidQualityCertificateArgumentException;
import com.khutircraftubackend.seller.qualityCertificates.exception.qualityException.QualityCertificateNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class QualityCertificateExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(QualityCertificateNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public QualityCertificateResponse handleQualityCertificateNotFoundException(
			QualityCertificateNotFoundException ex,
			HttpServletRequest request) {
		
		log.error("QualityCertificate not found: ", ex);
		
        return QualityCertificateResponse.builder()
				.status(HttpStatus.NOT_FOUND.value())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase())
				.message(ex.getLocalizedMessage())
				.path(request.getRequestURI())
				.build();
	}
	
	@ExceptionHandler(InvalidQualityCertificateArgumentException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public QualityCertificateResponse handleInvalidQualityCertificateArgumentException(
            InvalidQualityCertificateArgumentException ex,
            HttpServletRequest request) {
		
		log.error("Invalid Quality Certificate request : ", ex);
		
		return QualityCertificateResponse.builder()
				.status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                .message(ex.getLocalizedMessage())
                .path(request.getRequestURI())
                .build();
	}
	
}
