package mahydine_yanis.womenshop.model;

public class Product {

    private int id;
    private String name;
    private int quantity;
    private double sellPrice;
    private double purchasePrice;
    private boolean active;
    private Category category;

    public Product() {
    }

    public Product(int id, String name, int quantity, double sellPrice, double purchasePrice, boolean active, Category category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.sellPrice = sellPrice;
        this.purchasePrice = purchasePrice;
        this.active = active;
        this.category = category;
    }

    public Product(String name, int quantity, double sellPrice, double purchasePrice, boolean active, Category category) {
        this.name = name;
        this.quantity = quantity;
        this.sellPrice = sellPrice;
        this.purchasePrice = purchasePrice;
        this.active = active;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return name;
    }
}
