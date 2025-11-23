package mahydine_yanis.womenshop.daoimpl;

import mahydine_yanis.womenshop.model.Category;
import mahydine_yanis.womenshop.model.Product;
import mahydine_yanis.womenshop.util.Database;
import mahydine_yanis.womenshop.dao.ProductDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDaoImpl implements ProductDao {

    @Override
    public List<Product> getAll() {
        List<Product> result = new ArrayList<>();

        String sql = """
            SELECT p.id, p.name, p.quantity, p.sell_price, p.purchase_price, p.active,
                   c.id AS c_id, c.name AS c_name, c.discount_rate AS c_discount_rate, c.active_discount AS c_active_discount
            FROM product p
            JOIN category c ON p.category_id = c.id
            WHERE active = 1
            ORDER BY p.name
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<Product> findByCategory(Category category) {
        List<Product> result = new ArrayList<>();

        String sql = """
            SELECT p.id, p.name, p.quantity, p.sell_price, p.purchase_price, p.active,
                   c.id AS c_id, c.name AS c_name, c.discount_rate AS c_discount_rate, c.active_discount AS c_active_discount
            FROM product p
            JOIN category c ON p.category_id = c.id
            WHERE c.id = ?
            ORDER BY p.name
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, category.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Optional<Product> findById(int id) {
        String sql = """
            SELECT p.id, p.name, p.quantity, p.sell_price, p.purchase_price, p.active,
                   c.id AS c_id, c.name AS c_name, c.discount_rate AS c_discount_rate, c.active_discount AS c_active_discount
            FROM product p
            JOIN category c ON p.category_id = c.id
            WHERE p.id = ?
            """;

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
    public void save(Product product) {
        String sql = """
            INSERT INTO product (name, quantity, sell_price, purchase_price, active, category_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getName());
            ps.setInt(2, product.getQuantity());
            ps.setDouble(3, product.getSellPrice());
            ps.setDouble(4, product.getPurchasePrice());
            ps.setBoolean(5, product.isActive());
            ps.setInt(6, product.getCategory().getId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Product product) {
        String sql = """
            UPDATE product
            SET name = ?, quantity = ?, sell_price = ?, purchase_price = ?, active = ?, category_id = ?
            WHERE id = ?
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setInt(2, product.getQuantity());
            ps.setDouble(3, product.getSellPrice());
            ps.setDouble(4, product.getPurchasePrice());
            ps.setBoolean(5, product.isActive());
            ps.setInt(6, product.getCategory().getId());
            ps.setInt(7, product.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "UPDATE product SET active = 0 WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("c_id"));
        c.setName(rs.getString("c_name"));
        c.setDiscountRate(rs.getDouble("c_discount_rate"));
        c.setActiveDiscount(rs.getBoolean("c_active_discount"));

        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setQuantity(rs.getInt("quantity"));
        p.setSellPrice(rs.getDouble("sell_price"));
        p.setPurchasePrice(rs.getDouble("purchase_price"));
        p.setActive(rs.getBoolean("active"));
        p.setCategory(c);

        return p;
    }
}
