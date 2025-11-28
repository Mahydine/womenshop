package mahydine_yanis.womenshop.model;

public class Accessory extends Product {

    public Accessory() {
        super();
    }

    public Accessory(int id, String name, double salePrice, double purchasePrice, boolean active, Category category) {
        super(id, name, salePrice, purchasePrice, active, category);
    }

    public Accessory(String name, double salePrice, double purchasePrice, boolean active, Category category) {
        super(name, salePrice, purchasePrice, active, category);
    }
}
