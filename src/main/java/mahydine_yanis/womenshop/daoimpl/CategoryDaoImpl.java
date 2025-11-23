package mahydine_yanis.womenshop.daoimpl;

import mahydine_yanis.womenshop.dao.CategoryDao;
import mahydine_yanis.womenshop.model.Category;
import mahydine_yanis.womenshop.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDaoImpl implements CategoryDao {


    @Override
    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT id, name, discount_rate, active_discount FROM category ORDER BY name";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace(); // à remplacer plus tard par une gestion propre
        }

        return categories;
    }

    @Override
    public Optional<Category> findById(int id) {
        String sql = "SELECT id, name, discount_rate, active_discount FROM category WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void save(Category category) {
        String sql = "INSERT INTO category (name, discount_rate, active_discount) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getName());
            ps.setDouble(2, category.getDiscountRate());
            ps.setBoolean(3, category.isActiveDiscount());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    category.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Category category) {
        String sql = "UPDATE category SET name = ?, discount_rate = ?, active_discount = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getName());
            ps.setDouble(2, category.getDiscountRate());
            ps.setBoolean(3, category.isActiveDiscount());
            ps.setInt(4, category.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM category WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setDiscountRate(rs.getDouble("discount_rate"));
        c.setActiveDiscount(rs.getBoolean("active_discount"));
        return c;
    }
}