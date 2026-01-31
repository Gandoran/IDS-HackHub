package unicam.it.idshackhub.service.paypal;

import com.paypal.orders.Order;

/**
 * Handles the interaction with PayPal API for creating and capturing orders.
 * <p>
 * This class follows SOLID principles by delegating credential management
 * to {@link PayPalConfig} and focusing solely on order lifecycle execution.
 * </p>
 */
public interface PayPal {
    /**
     * Creates a PayPal order with a specific payee.
     *
     * @param amount the payment amount as a string.
     * @param email  the PayPal email address of the recipient (payee).
     * @return the created {@link Order} or null if the request fails.
     */
    Order createOrder(String amount, String email);
    /**
     * Captures an authorized payment for the given order ID.
     *
     * @param orderId the unique identifier of the PayPal order.
     * @return true if the payment status is COMPLETED, false otherwise.
     */
    boolean captureOrder(String orderId);
}
