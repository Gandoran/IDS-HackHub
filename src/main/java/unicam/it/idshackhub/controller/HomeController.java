package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.service.PayPalService;
import unicam.it.idshackhub.service.UserService;

import java.util.Map;

@RestController
public class HomeController {

    @Autowired
    private UserService userService;
    @Autowired
    private PayPalService payPalService;

    @GetMapping("/user-info")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return Map.of("error", "Not logged in");

        String githubId = String.valueOf(principal.getAttributes().get("id"));
        String login = principal.getAttribute("login");
        String email = principal.getAttribute("email");
        String avatar = principal.getAttribute("avatar_url");

        User dbUser = userService.processOAuthPostLogin(githubId, login, email);


        return Map.of(
                "name", dbUser.getUsername(),
                "db_id", dbUser.getId(),
                "role", dbUser.getGlobalRole(),
                "avatar", avatar != null ? avatar : ""
        );
    }

    //JUST FOR TEST PURPOSE OF THE PAYMENT API
    @GetMapping("/test-payment")
    public RedirectView testPayment() {
        String approvalLink = payPalService.initiatePayment(150.00, "giacomo.carloni@unicam.studenti.it");
        return new RedirectView(approvalLink);
    }

    @GetMapping("/api/payments/success")
    public ResponseEntity<String> confirmPaymentTest(
            @RequestParam("token") String orderId,
            @RequestParam(value = "PayerID", required = false) String payerId) {
        try {
            boolean success = payPalService.confirmPayment(orderId);

            if (success) {
                return ResponseEntity.ok("<h1>Pagamento Completato!</h1><p>Ordine: " + orderId + "</p>");
            } else {
                return ResponseEntity.badRequest().body("<h1>Errore nella cattura del pagamento.</h1>");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Errore di sistema: " + e.getMessage());
        }
    }
}