package com.agprogramming.seom_v2_bff.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class SeomCashBalance extends SeomPaymentMethod {
	private double balance;
	
	public SeomCashBalance() {
		super("cash-balance");
	}
	
	public SeomCashBalance(double balance) {
		super("cash-balance");
		
		this.balance = balance;
	}
}
