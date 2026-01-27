package unicam.it.idshackhub.service.paypal;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * Service class responsible for initializing and managing the PayPal HTTP client.
 * <p>
 * This service loads sensitive credentials (Client ID and Client Secret) from
 * environment variables using the {@code Dotenv} library to ensure security
 * and separation of configuration from code.
 * </p>
 * * <p>
 * Currently configured to operate within the <b>PayPal Sandbox</b> environment
 * for testing purposes.
 * </p>
 */
@Getter
@Component
public class PayPalConfig {

    /**
     * The unique identifier for the PayPal application, retrieved from environment variables.
     */
    private final String clientId = Dotenv.load().get("PAYPALCLIENTID");

    /**
     * The secret key for the PayPal application, retrieved from environment variables.
     */
    private final String clientSecret = Dotenv.load().get("PAYPALCLIENTSECRET");

    /**
     * The configured HTTP client used to execute requests against the PayPal API.
     * @return an instance of {@link PayPalHttpClient}
     */
    private final PayPalHttpClient httpClient;

    /**
     * Constructs a new {@code PayPalService}.
     * <p>
     * Initializes the {@link PayPalEnvironment} in Sandbox mode and sets up the
     * {@link PayPalHttpClient} with the credentials loaded from the .env file.
     * </p>
     * * @throws RuntimeException if the environment variables are missing or null.
     */
    protected PayPalConfig() {
        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
        httpClient = new PayPalHttpClient(environment);
    }
}
