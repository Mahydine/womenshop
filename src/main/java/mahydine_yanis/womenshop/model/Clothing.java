package mahydine_yanis.womenshop.model;

public class Clothing extends Product {

    private int size;

    public Clothing() {
        super();
    }

    public Clothing(int id, String name, double salePrice, double purchasePrice, boolean active, Category category, int size) {
        super(id, name, salePrice, purchasePrice, active, category);
        setSize(size);
    }

    public Clothing(String name, double salePrice, double purchasePrice, boolean active, Category category, int size) {
        super(name, salePrice, purchasePrice, active, category);
        setSize(size);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size < 34 || size > 54) {
            throw new IllegalArgumentException("Clothing size must be between 34 and 54");
        }
        this.size = size;
    }
}
