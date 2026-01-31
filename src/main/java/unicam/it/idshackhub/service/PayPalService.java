package unicam.it.idshackhub.service;

import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.service.paypal.PayPal;
import unicam.it.idshackhub.service.paypal.PayPalOrder;

/**
 * Provides operations related to PayPal payments.
 * <p>
 *     This service orchestrates the payment process by delegating the low-level
 * </p>
 */
@Service
public class PayPalService {
    private final PayPal payPal;

    /**
     * Constructs the service with an injected {@link PayPalOrder} component.
     *
     * @param payPal the component responsible for low-level PayPal API calls.
     */
    @Autowired
    public PayPalService(PayPal payPal) {
        this.payPal = payPal;
    }

    /**
     * Initiates the payment process by creating a PayPal order.
     * <p>
     *     The winner's email address is passed as a parameter
     *     to ensure that the payment is properly attributed to the
     *     correct team.
     * </p>
     */
    public String initiatePayment(Double amount,String winnerEmail) {
        Order order = payPal.createOrder(amount.toString(),winnerEmail);
        if (order == null) throw new RuntimeException("Error during the payment process.");
        return order.links().stream()
                .filter(link -> "approve".equals(link.rel()))
                .findFirst()
                .map(LinkDescription::href)
                .orElseThrow(() -> new RuntimeException("Link not found."));
    }

    /**
     * Finalizes the transaction by capturing the payment.
     * <p>
     * This method is transactional to ensure that any database updates
     * following the payment are handled atomically.
     * </p>
     *
     * @param orderId     the ID of the order to capture.
     * @throws RuntimeException if the capture process fails.
     */
    @Transactional
    public void confirmPayment(String orderId){
        boolean success = payPal.captureOrder(orderId);
        if (!success) {throw new RuntimeException("Failed to capture PayPal order with ID: " + orderId);}
    }
}
