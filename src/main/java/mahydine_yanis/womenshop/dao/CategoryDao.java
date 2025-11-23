package mahydine_yanis.womenshop.dao;

import mahydine_yanis.womenshop.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {

    List<Category> getAll();

    Optional<Category> findById(int id);

    void save(Category category);

    void update(Category category);

    void delete(int id);
}