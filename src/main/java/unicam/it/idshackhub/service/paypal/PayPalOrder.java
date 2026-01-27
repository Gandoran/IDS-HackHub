package unicam.it.idshackhub.service.paypal;

import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Handles the interaction with PayPal API for creating and capturing orders.
 * <p>
 * This class follows SOLID principles by delegating credential management
 * to {@link PayPalConfig} and focusing solely on order lifecycle execution.
 * </p>
 */
@Component
public class PayPalOrder {

    private final PayPalHttpClient httpClient;

    /**
     * Constructs a {@code PayPalOrder} with the injected PayPal HTTP client.
     *
     * @param payPalService the service providing the configured PayPal client.
     */
    @Autowired
    public PayPalOrder(PayPalConfig payPalService) {
        this.httpClient = payPalService.getHttpClient();
    }

    /**
     * Creates a PayPal order with a specific payee.
     *
     * @param amount the payment amount as a string.
     * @param email  the PayPal email address of the recipient (payee).
     * @return the created {@link Order} or null if the request fails.
     */
    public Order createOrder(String amount, String email) {
        OrderRequest orderRequest = buildOrderRequest(amount, email);
        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
        try {
            return httpClient.execute(request).result();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create PayPal order", e);
        }
    }

    /**
     * Captures an authorized payment for the given order ID.
     *
     * @param orderId the unique identifier of the PayPal order.
     * @return true if the payment status is COMPLETED, false otherwise.
     */
    public boolean captureOrder(String orderId) {
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        request.requestBody(new OrderRequest());
        try {
            Order result = httpClient.execute(request).result();
            return "COMPLETED".equals(result.status());
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Helper method to encapsulate the logic of building the OrderRequest.
     * (Respects Single Responsibility by separating building logic from execution)
     */
    private OrderRequest buildOrderRequest(String amount, String email) {
        return new OrderRequest()
                .checkoutPaymentIntent("CAPTURE")
                .applicationContext(new ApplicationContext()
                        .returnUrl("http://localhost:8080/api/payments/success")
                        .cancelUrl("http://localhost:8080/api/payments/cancel"))
                .purchaseUnits(List.of(new PurchaseUnitRequest()
                        .amountWithBreakdown(new AmountWithBreakdown()
                                .currencyCode("EUR")
                                .value(amount))
                        .payee(new Payee().email(email))));
    }
}
