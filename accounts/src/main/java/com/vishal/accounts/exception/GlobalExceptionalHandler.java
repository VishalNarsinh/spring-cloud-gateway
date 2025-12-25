package com.vishal.accounts.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishal.accounts.dto.ErrorResponseDto;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionalHandler extends ResponseEntityExceptionHandler {

	private final ObjectMapper objectMapper;

	public GlobalExceptionalHandler(ObjectMapper objectMapper) {this.objectMapper = objectMapper;}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
			WebRequest request) {
		/*Map<String, String> validationErrors = new HashMap<>();
		List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();

		validationErrorList.forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String validationMsg = error.getDefaultMessage();
			validationErrors.put(fieldName, validationMsg);
		});
		return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);*/
		String errorMessage = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.distinct()
				.collect(Collectors.joining(", "));
		ErrorResponseDto errorResponseDto = new ErrorResponseDto(getOnlyUriFromWebRequest(request), HttpStatus.BAD_REQUEST, errorMessage,
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception, WebRequest webRequest) {
		ErrorResponseDto errorResponseDTO = new ErrorResponseDto(getOnlyUriFromWebRequest(webRequest), HttpStatus.INTERNAL_SERVER_ERROR,
				exception.getMessage(), LocalDateTime.now());
		return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest webRequest) {
		ErrorResponseDto errorResponseDTO = new ErrorResponseDto(getOnlyUriFromWebRequest(webRequest), HttpStatus.NOT_FOUND, exception.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(CustomerAlreadyExistsException.class)
	public ResponseEntity<ErrorResponseDto> handleCustomerAlreadyExistsException(CustomerAlreadyExistsException exception, WebRequest webRequest) {
		ErrorResponseDto errorResponseDTO = new ErrorResponseDto(getOnlyUriFromWebRequest(webRequest), HttpStatus.BAD_REQUEST, exception.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DuplicateMobileNumberException.class)
	public ResponseEntity<ErrorResponseDto> handleDuplicateMobileNumberException(DuplicateMobileNumberException exception, WebRequest webRequest) {
		ErrorResponseDto errorResponseDTO = new ErrorResponseDto(getOnlyUriFromWebRequest(webRequest), HttpStatus.CONFLICT, exception.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponseDTO, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException exception, WebRequest webRequest) {
		String errorMessage = exception.getConstraintViolations()
				.stream()
				.map(jakarta.validation.ConstraintViolation::getMessage)
				.distinct()
				.collect(Collectors.joining(", "));
		ErrorResponseDto errorResponseDTO = new ErrorResponseDto(getOnlyUriFromWebRequest(webRequest), HttpStatus.BAD_REQUEST, errorMessage,
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest webRequest) {
		String dbMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

		ConstraintViolation violation = ConstraintViolation.fromMessage(dbMessage);
		String message = ConstraintViolation.resolveFriendlyMessage(dbMessage);

		ErrorResponseDto errorResponse = new ErrorResponseDto("uri=" + getOnlyUriFromWebRequest(webRequest), violation.getStatus(), message,
				LocalDateTime.now());

		return new ResponseEntity<>(errorResponse, violation.getStatus());
	}


	@ExceptionHandler(FeignException.class)
	public ResponseEntity<ErrorResponseDto> handleFeignException(FeignException exception){
		ErrorResponseDto errorResponseDto;
		try {
			errorResponseDto = objectMapper.readValue(exception.contentUTF8(), ErrorResponseDto.class);
		} catch (JsonProcessingException e) {
			errorResponseDto = new ErrorResponseDto(
					null,                                // apiPath (unknown)
					HttpStatus.INTERNAL_SERVER_ERROR,    // errorCode
					"Unable to parse error response from downstream service",
					LocalDateTime.now()
			);
		}
		HttpStatus status = HttpStatus.resolve(exception.status());
		if(status == null){
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return ResponseEntity.status(status).body(errorResponseDto);
	}

	private static final Map<String, String> CONSTRAINT_MESSAGES = Map.of("mobile_number_UNIQUE", "Duplicate mobile number. This number already exists.",
			"email_UNIQUE", "Duplicate email. This email is already in use.");

	private String extractMeaningfulMessage(String message) {
		if (message.contains("Duplicate entry")) {
			for (Map.Entry<String, String> entry : CONSTRAINT_MESSAGES.entrySet()) {
				if (message.contains(entry.getKey())) {
					return entry.getValue();
				}
			}
			return "Duplicate entry. A unique constraint was violated.";
		}
		return message;
	}



	private String getOnlyUriFromWebRequest(WebRequest webRequest) {
		String requestURI = ((ServletWebRequest) webRequest).getRequest().getRequestURI();
		return HtmlUtils.htmlEscape(requestURI);
	}

}
