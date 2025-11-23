package mahydine_yanis.womenshop.model;

public class Category {

    private int id;
    private String name;
    private double discountRate;
    private boolean activeDiscount;

    public Category() {
    }

    public Category(int id, String name, double discountRate, boolean activeDiscount) {
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.activeDiscount = activeDiscount;
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, double discountRate, boolean activeDiscount) {
        this.name = name;
        this.discountRate = discountRate;
        this.activeDiscount = activeDiscount;
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

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public boolean isActiveDiscount() {
        return activeDiscount;
    }

    public void setActiveDiscount(boolean activeDiscount) {
        this.activeDiscount = activeDiscount;
    }

    @Override
    public String toString() {
        return name;
    }
}
