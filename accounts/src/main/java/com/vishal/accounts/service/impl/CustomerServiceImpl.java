package com.vishal.accounts.service.impl;

import com.vishal.accounts.dto.AccountsDto;
import com.vishal.accounts.dto.CardsDto;
import com.vishal.accounts.dto.CustomerDetailsDto;
import com.vishal.accounts.dto.LoansDto;
import com.vishal.accounts.entity.Accounts;
import com.vishal.accounts.entity.Customer;
import com.vishal.accounts.exception.ResourceNotFoundException;
import com.vishal.accounts.mapper.AccountsMapper;
import com.vishal.accounts.mapper.CustomerMapper;
import com.vishal.accounts.repository.AccountsRepository;
import com.vishal.accounts.repository.CustomerRepository;
import com.vishal.accounts.service.ICustomerService;
import com.vishal.accounts.service.client.CardsFeignClient;
import com.vishal.accounts.service.client.LoansFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

	private final CustomerRepository customerRepository;
	private final AccountsRepository accountsRepository;
	private final CardsFeignClient cardsFeignClient;
	private final LoansFeignClient loansFeignClient;

	@Override
	public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
		Customer customer = customerRepository.findByMobileNumber(mobileNumber)
				.orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
				.orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));
		CardsDto cardsDto = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber).getBody();
		LoansDto loansDto = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber).getBody();
		CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
		customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
		customerDetailsDto.setCardsDto(cardsDto);
		customerDetailsDto.setLoansDto(loansDto);
		return customerDetailsDto;
	}
}
