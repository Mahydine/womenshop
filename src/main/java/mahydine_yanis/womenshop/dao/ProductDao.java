package mahydine_yanis.womenshop.dao;

import mahydine_yanis.womenshop.model.Category;
import mahydine_yanis.womenshop.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    List<Product> getAll();

    List<Product> findByCategory(Category category);

    Optional<Product> findById(int id);

    void save(Product product);

    void update(Product product);

    void delete(int id);
}
