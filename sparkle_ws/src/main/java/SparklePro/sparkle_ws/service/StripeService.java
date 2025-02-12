package SparklePro.sparkle_ws.service;

import SparklePro.sparkle_ws.dbaccess.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    // stripe API
    // -> productName, amount, quantity, currency
    // -> return sessionId and url
    public StripeResponse checkoutProducts(List<ProductRequest> productRequests) {
        // Set your secret key. Remember to switch to your live secret key in production!
        Stripe.apiKey = secretKey;

        // Create line items from the product requests
        SessionCreateParams.Builder sessionParamsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/J2EE_CA2_p2340205_p2340490/ConfirmBookingServlet?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:8080/cancel");

        for (ProductRequest productRequest : productRequests) {
            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(productRequest.getName())
                            .build();

            // Create price data for each product
            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "SGD")
                            .setUnitAmount(productRequest.getAmount())
                            .setProductData(productData)
                            .build();

            // Create the line item for this product
            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(productRequest.getQuantity())
                            .setPriceData(priceData)
                            .build();

            // Add the line item to the session
            sessionParamsBuilder.addLineItem(lineItem);
        }

        // Create a session with the line items
        SessionCreateParams params = sessionParamsBuilder.build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            // Log the error
            e.printStackTrace();
        }

        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment session created")
                .sessionId(session != null ? session.getId() : "")
                .sessionUrl(session != null ? session.getUrl() : "")
                .build();
    }
}
