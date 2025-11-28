package mahydine_yanis.womenshop.model;

public abstract class Product {

    private int id;
    private String name;
    private int quantity = 0;
    private double salePrice;
    private double purchasePrice;
    /**
     * Optional discounted price. When strictly positive, this price is used
     * instead of the base sale price.
     */
    private double discountPrice = 0.0;
    private boolean active;
    private Category category;

    protected Product() {
    }

    protected Product(int id, String name, double salePrice, double purchasePrice, boolean active, Category category) {
        this(name, salePrice, purchasePrice, active, category);
        this.id = id;
    }

    protected Product(String name, double salePrice, double purchasePrice, boolean active, Category category) {
        setName(name);
        setSalePrice(salePrice);
        setPurchasePrice(purchasePrice);
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
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public double getSalePrice() {
        return salePrice;
    }

    /**
     * Sale price is fixed. Validation prevents negative values and a value
     * lower than the purchase price.
     */
    public void setSalePrice(double salePrice) {
        if (salePrice < 0) {
            throw new IllegalArgumentException("Sale price cannot be negative");
        }
        if (purchasePrice > 0 && salePrice < purchasePrice) {
            throw new IllegalArgumentException("Sale price cannot be lower than purchase price");
        }
        this.salePrice = salePrice;
    }

    public double getSellPrice() {
        return salePrice;
    }

    public void setSellPrice(double salePrice) {
        setSalePrice(salePrice);
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        if (purchasePrice < 0) {
            throw new IllegalArgumentException("Purchase price cannot be negative");
        }
        if (salePrice > 0 && purchasePrice > salePrice) {
            throw new IllegalArgumentException("Purchase price cannot exceed sale price");
        }
        this.purchasePrice = purchasePrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        if (discountPrice < 0) {
            throw new IllegalArgumentException("Discount price cannot be negative");
        }
        this.discountPrice = discountPrice;
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

    /**
     * Returns the price used for a sale: discount price if defined, otherwise
     * the fixed sale price.
     */
    public double getEffectivePrice() {
        return discountPrice > 0 ? discountPrice : salePrice;
    }

    /**
     * Applies a discount based on a rate (e.g. 0.3 for 30%).
     */
    public void applyDiscountRate(double rate) {
        if (rate < 0) {
            throw new IllegalArgumentException("Discount rate cannot be negative");
        }
        if (rate == 0) {
            this.discountPrice = 0;
            return;
        }
        this.discountPrice = salePrice * (1 - rate);
    }

    public void clearDiscount() {
        this.discountPrice = 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
