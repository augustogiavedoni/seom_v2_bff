package com.agprogramming.seom_v2_bff.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agprogramming.seom_v2_bff.payloads.response.ReceiptUrl;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {
	@Value("${seom_auth.app.stripeApiKey}")
	private String stripeApiKey;
	
	@GetMapping()
	public ResponseEntity<?> getReceiptUrl(@Valid @RequestParam String chargeId) throws StripeException {
		Stripe.apiKey = stripeApiKey;

		Charge charge = Charge.retrieve(chargeId);
		
		ReceiptUrl receiptUrl = new ReceiptUrl();

		receiptUrl.setUrl(charge.getReceiptUrl());

		return ResponseEntity.ok(receiptUrl);
	}
}
