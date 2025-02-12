package SparklePro.sparkle_ws.dbaccess;

import java.util.List;
import java.util.Map;

public class BookingRequest {
    private int customerId;
    private List<Map<String, Object>> cart;
    private double subtotalAmount;
    private double gstAmount;
    private double totalAmount;
    private String paymentMethod;
    private String transactionRef;

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public List<Map<String, Object>> getCart() {
        return cart;
    }

    public void setCart(List<Map<String, Object>> cart) {
        this.cart = cart;
    }

    public double getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(double subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public double getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(double gstAmount) {
        this.gstAmount = gstAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }
}