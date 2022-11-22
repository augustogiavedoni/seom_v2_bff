package com.agprogramming.seom_v2_bff.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data()
@EqualsAndHashCode(callSuper=true)
public class CreditCard extends SeomPaymentMethod {
	private String id;
	private String brand;
	@JsonProperty("expiry_month")
	private Long expiryMonth;
	@JsonProperty("expiry_year")
	private Long expiryYear;
	@JsonProperty("last_four_digits")
	private String lastFourDigits;
	
	public CreditCard() {
		super("credit");
	}
	
	public CreditCard(String id, String brand, Long expiryMonth, Long expiryYear, String lastFourDigits) {
		super("credit");
		
		this.id = id;
		this.brand = brand;
		this.expiryMonth = expiryMonth;
		this.expiryYear = expiryYear;
		this.lastFourDigits = lastFourDigits;
	}
}
