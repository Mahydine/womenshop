package mahydine_yanis.womenshop.model;

import mahydine_yanis.womenshop.util.StockOperationType;

import java.time.LocalDateTime;

public class StockOperation {

    private int id;
    private int quantity;
    private LocalDateTime createdAt;
    private double unitPrice;
    private StockOperationType type;
    private Product product;

    public StockOperation() {
    }

    public StockOperation(int id, int quantity, LocalDateTime createdAt, double unitPrice, StockOperationType type, Product product) {
        this.id = id;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.unitPrice = unitPrice;
        this.type = type;
        this.product = product;
    }

    public StockOperation(int quantity, LocalDateTime createdAt, double unitPrice, StockOperationType type, Product product) {
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.unitPrice = unitPrice;
        this.type = type;
        this.product = product;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public StockOperationType getType() {
        return type;
    }

    public void setType(StockOperationType type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return type + " " + quantity + " x " + unitPrice + " on " + createdAt;
    }
}
