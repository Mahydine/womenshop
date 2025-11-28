package mahydine_yanis.womenshop.model;

public class Shoes extends Product {

    private int size;

    public Shoes() {
        super();
    }

    public Shoes(int id, String name, double salePrice, double purchasePrice, boolean active, Category category, int size) {
        super(id, name, salePrice, purchasePrice, active, category);
        setSize(size);
    }

    public Shoes(String name, double salePrice, double purchasePrice, boolean active, Category category, int size) {
        super(name, salePrice, purchasePrice, active, category);
        setSize(size);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size < 36 || size > 50) {
            throw new IllegalArgumentException("Shoes size must be between 36 and 50");
        }
        this.size = size;
    }
}
