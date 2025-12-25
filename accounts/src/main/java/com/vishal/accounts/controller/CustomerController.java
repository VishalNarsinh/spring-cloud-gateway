package com.vishal.accounts.controller;

import com.vishal.accounts.dto.CustomerDetailsDto;
import com.vishal.accounts.dto.ErrorResponseDto;
import com.vishal.accounts.service.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "REST APIs for Customer in microservice", description = "REST APIs in microservice to FETCH customer details")
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@Slf4j
public class CustomerController {

	private final ICustomerService iCustomerService;

	@Operation(
			summary = "Fetch Customer Details REST API",
			description = "REST API to fetch Customer details based on a mobile number"
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "HTTP Status OK"
			),
			@ApiResponse(
					responseCode = "500",
					description = "HTTP Status Internal Server Error",
					content = @Content(
							schema = @Schema(implementation = ErrorResponseDto.class)
					)
			)
	}
	)
	@GetMapping(value = "/customerDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(
			@RequestHeader("correlation-id") String correlationId,
			@RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
		log.info("correllation-id: {}",correlationId);
		return ResponseEntity.ok(iCustomerService.fetchCustomerDetails(mobileNumber,correlationId ));
	}
}
