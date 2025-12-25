package com.vishal.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "CustomerDetails", description = "Schema to hold Customer,Account,Cards & Loans information")
public class CustomerDetailsDto {

	private String name;

	private String email;

	private String mobileNumber;

	private AccountsDto accountsDto;

	private LoansDto loansDto;

	private CardsDto cardsDto;
}
