package com.agprogramming.seom_v2_bff.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agprogramming.seom_v2_bff.models.CreditCard;
import com.agprogramming.seom_v2_bff.models.DebitCard;
import com.agprogramming.seom_v2_bff.models.SeomPaymentMethod;
import com.agprogramming.seom_v2_bff.models.SeomCashBalance;
import com.agprogramming.seom_v2_bff.payloads.request.AddPaymentMethodRequest;
import com.stripe.Stripe;
import com.stripe.model.PaymentMethod;
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
		
		List<SeomPaymentMethod> paymentMethods = new ArrayList<SeomPaymentMethod>();

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

	@PostMapping()
	public ResponseEntity<SeomPaymentMethod> addPaymentMethod(@Valid @RequestBody AddPaymentMethodRequest request) throws StripeException {
		Stripe.apiKey = stripeApiKey;
		
		SeomPaymentMethod seomPaymentMethod;
		Map<String, Object> cardInformation = new HashMap<>();

		cardInformation.put("number", request.getCardNumber());
		cardInformation.put("exp_month", request.getExpiryMonth());
		cardInformation.put("exp_year", request.getExpiryYear());
		cardInformation.put("cvc", request.getSecurityCode());

		Map<String, Object> params = new HashMap<>();

		params.put("type", "card");
		params.put("card", cardInformation);

		PaymentMethod paymentMethod = PaymentMethod.create(params);

		PaymentMethod attachedPaymentMethod = attachPaymentMethodToCustomer(request.getCustomerId(), paymentMethod);
		
		Card card = attachedPaymentMethod.getCard();
		
		if (card.getFunding().equals("credit")) {
			seomPaymentMethod = new CreditCard(paymentMethod.getId(), card.getBrand(), card.getExpMonth(), card.getExpYear(), card.getLast4());
		} else {
			seomPaymentMethod = new DebitCard(paymentMethod.getId(), card.getBrand(), card.getExpMonth(), card.getExpYear(), card.getLast4());
		}

		return ResponseEntity.ok(seomPaymentMethod);
	}

	private PaymentMethod attachPaymentMethodToCustomer(String customerId, PaymentMethod paymentMethod) throws StripeException {
		Map<String, Object> params = new HashMap<>();
		
		params.put("customer", customerId);

		PaymentMethod updatedPaymentMethod = paymentMethod.attach(params);

		return updatedPaymentMethod;
	}

	@DeleteMapping()
	public ResponseEntity<?> deletePaymentMethod(@Valid @RequestParam String paymentMethodId) throws StripeException {
		Stripe.apiKey = stripeApiKey;
		
		PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

		paymentMethod.detach();
		
		return ResponseEntity.ok(null);
	}
}
