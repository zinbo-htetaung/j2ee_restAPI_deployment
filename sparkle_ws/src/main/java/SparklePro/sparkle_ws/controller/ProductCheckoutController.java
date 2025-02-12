package SparklePro.sparkle_ws.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import SparklePro.sparkle_ws.dbaccess.*;
import SparklePro.sparkle_ws.service.StripeService;

import java.util.List;

@RestController
@RequestMapping("/api/stripe")
@CrossOrigin(origins = "http://localhost:8080")
public class ProductCheckoutController {

    private final StripeService stripeService;

    public ProductCheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody List<ProductRequest> productRequests) {
        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequests);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }
}
