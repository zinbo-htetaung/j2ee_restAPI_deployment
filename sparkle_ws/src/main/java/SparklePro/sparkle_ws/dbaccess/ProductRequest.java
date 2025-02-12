package SparklePro.sparkle_ws.dbaccess;

public class ProductRequest {
    private Long amount;
    private Long quantity;
    private String name;
    private String currency;

    public ProductRequest() {}
    
    public ProductRequest(Long amount, Long quantity, String name, String currency) {
        this.amount = amount;
        this.quantity = quantity;
        this.name = name;
        this.currency = currency;
    }

    // Getters
    public Long getAmount() {
        return amount;
    }

    public Long getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    // Setters
    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

