package com.agprogramming.seom_v2_bff.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agprogramming.seom_v2_bff.models.CreditCard;
import com.agprogramming.seom_v2_bff.models.DebitCard;
import com.agprogramming.seom_v2_bff.models.PaymentMethod;
import com.agprogramming.seom_v2_bff.models.SeomCashBalance;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod.Card;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.param.CustomerListPaymentMethodsParams;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodsController {
	@Value("${seom_auth.app.stripeApiKey}")
	private String stripeApiKey;
	
	@GetMapping()
	public ResponseEntity<?> getPaymentMethods(@Valid @RequestParam String customerId) throws StripeException {
		Stripe.apiKey = stripeApiKey;

		List<PaymentMethod> paymentMethods = new ArrayList<PaymentMethod>();

		Customer customer = Customer.retrieve(customerId);

		CustomerListPaymentMethodsParams params = CustomerListPaymentMethodsParams.builder()
				.setType(CustomerListPaymentMethodsParams.Type.CARD).build();

		PaymentMethodCollection rawPaymentMethods = customer.listPaymentMethods(params);

		Long cashBalance = customer.getBalance();

		if (cashBalance != null) {
			SeomCashBalance seomCashBalance = new SeomCashBalance();

			seomCashBalance.setBalance(cashBalance / -100.00);

			paymentMethods.add(seomCashBalance);
		}

		rawPaymentMethods.getData().forEach((paymentMethod) -> {
			Card card = paymentMethod.getCard();

			if (card != null) {
				if (card.getFunding().equals("credit")) {
					CreditCard creditCard = new CreditCard();

					creditCard.setBrand(card.getBrand());
					creditCard.setExpiryMonth(card.getExpMonth());
					creditCard.setExpiryYear(card.getExpYear());
					creditCard.setId(paymentMethod.getId());
					creditCard.setLastFourDigits(card.getLast4());

					paymentMethods.add(creditCard);
				} else if (card.getFunding().equals("debit")) {
					DebitCard debitCard = new DebitCard();

					debitCard.setBrand(card.getBrand());
					debitCard.setExpiryMonth(card.getExpMonth());
					debitCard.setExpiryYear(card.getExpYear());
					debitCard.setId(paymentMethod.getId());
					debitCard.setLastFourDigits(card.getLast4());

					paymentMethods.add(debitCard);
				}
			}
		});

		return ResponseEntity.ok(paymentMethods);
	}
}
