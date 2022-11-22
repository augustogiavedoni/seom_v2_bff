package com.agprogramming.seom_v2_bff.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class SeomPaymentMethod {
	private String type;
}
