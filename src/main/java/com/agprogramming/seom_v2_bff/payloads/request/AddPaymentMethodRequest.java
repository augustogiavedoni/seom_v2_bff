package com.agprogramming.seom_v2_bff.payloads.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddPaymentMethodRequest {
	@NotBlank
	@JsonProperty("card_number")
	String cardNumber;
	
	@NotBlank
	@JsonProperty("security_code")
	String securityCode;
	
	@NotNull
	@JsonProperty("expiry_month")
	Long expiryMonth;
	
	@NotNull
	@JsonProperty("expiry_year")
	Long expiryYear;
	
	@NotBlank
	@JsonProperty("customer_id")
	String customerId;
}
