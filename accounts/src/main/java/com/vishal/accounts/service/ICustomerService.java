package com.vishal.accounts.service;

import com.vishal.accounts.dto.CustomerDetailsDto;

public interface ICustomerService {

	/*
	 * @param mobileNumber - Input Mobile Number
	 * @return CustomerDetailsDto - Customer Details based on mobile number
	 */
	CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId);
}
