package com.agprogramming.seom_v2_bff.controllers;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agprogramming.seom_v2_bff.exceptions.CardDeclinedException;
import com.agprogramming.seom_v2_bff.exceptions.ParkingTicketNotFoundException;
import com.agprogramming.seom_v2_bff.models.ParkingTicket;
import com.agprogramming.seom_v2_bff.models.Receipt;
import com.agprogramming.seom_v2_bff.payloads.request.PaymentRequest;
import com.agprogramming.seom_v2_bff.repository.ParkingTicketRepository;
import com.agprogramming.seom_v2_bff.repository.ReceiptRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.CustomerBalanceTransaction;
import com.stripe.model.PaymentIntent;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
	@Value("${seom_auth.app.stripeApiKey}")
	private String stripeApiKey;

	@Autowired
	ParkingTicketRepository parkingTicketRepository;

	@Autowired
	ReceiptRepository receiptRepository;

	@PostMapping()
	public ResponseEntity<?> executePayment(@Valid @RequestBody PaymentRequest paymentRequest) throws StripeException {
		Stripe.apiKey = stripeApiKey;

		// DecimalFormat decimalFormatter = new DecimalFormat("0.00");
		// decimalFormatter.setMaximumFractionDigits(2);

		ParkingTicket parkingTicket = parkingTicketRepository.findById(paymentRequest.getParkingTicketId())
				.orElseThrow(() -> new ParkingTicketNotFoundException("/api/payment"));
		Receipt receipt = parkingTicket.getReceipt();
		int amount = (int) (Float.parseFloat(receipt.getAmount()) * 100.00f);

		try {

			if (paymentRequest.isAccountBalance()) {
				Map<String, Object> transactionInformation = new HashMap<>();
				Customer customer = Customer.retrieve(paymentRequest.getStripeUserId());

				transactionInformation.put("amount", amount);
				transactionInformation.put("currency", "ars");

				CustomerBalanceTransaction balanceTransaction = customer.balanceTransactions()
						.create(transactionInformation);

				receipt.setChargeId(balanceTransaction.getId());
			} else {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				String parkingDescription = "Estacionamiento del día " + parkingTicket.getDate().format(formatter)
						+ " desde las " + parkingTicket.getStartTime().toString() + " hasta las "
						+ parkingTicket.getEndTime().toString() + ".";
				Map<String, Object> paymentInformation = new HashMap<>();

				paymentInformation.put("amount", amount);
				paymentInformation.put("currency", "ars");
				paymentInformation.put("customer", paymentRequest.getStripeUserId());
				paymentInformation.put("confirm", true);
				paymentInformation.put("payment_method", paymentRequest.getPaymentMethodId());
				paymentInformation.put("description", parkingDescription);

				PaymentIntent paymentIntent = PaymentIntent.create(paymentInformation);

				receipt.setChargeId(paymentIntent.getLatestCharge());
				receipt.setHasGeneratedReceipt(true);
			}
		} catch (StripeException exception) {
			if (exception.getCode().equals("card_declined")) {
				throw new CardDeclinedException("/api/payment");
			} else {
				throw exception;
			}
		}

		receipt.setPaid(true);

		Receipt updatedReceipt = receiptRepository.save(receipt);

		return ResponseEntity.ok(updatedReceipt);
	}

	@GetMapping()
	public ResponseEntity<?> getReceiptUrl(@Valid @RequestParam String chargeId) throws StripeException {
		Stripe.apiKey = stripeApiKey;

		Charge charge = Charge.retrieve(chargeId);

		String receiptUrl = charge.getReceiptUrl();

		return ResponseEntity.ok(receiptUrl);
	}
}
