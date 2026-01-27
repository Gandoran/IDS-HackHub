package unicam.it.idshackhub.service;

import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.service.paypal.PayPalOrder;

@Service
public class PayPalService {
    private final PayPalOrder payPalOrder;

    /**
     * Constructs the service with an injected {@link PayPalOrder} component.
     *
     * @param payPalOrder the component responsible for low-level PayPal API calls.
     */
    @Autowired
    public PayPalService(PayPalOrder payPalOrder) {
        this.payPalOrder = payPalOrder;
    }

    public String initiatePayment(Double amount,String winnerEmail) {
        Order order = payPalOrder.createOrder(amount.toString(),winnerEmail);
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
        boolean success = payPalOrder.captureOrder(orderId);
        if (!success) {throw new RuntimeException("Failed to capture PayPal order with ID: " + orderId);}
    }
}
