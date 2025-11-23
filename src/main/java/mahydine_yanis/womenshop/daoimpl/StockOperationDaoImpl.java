package mahydine_yanis.womenshop.daoimpl;

import mahydine_yanis.womenshop.model.Product;
import mahydine_yanis.womenshop.model.StockOperation;
import mahydine_yanis.womenshop.util.StockOperationType;
import mahydine_yanis.womenshop.util.Database;
import mahydine_yanis.womenshop.dao.StockOperationDao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StockOperationDaoImpl implements StockOperationDao {

    @Override
    public void save(StockOperation operation) {
        String sqlInsert = """
            INSERT INTO stock_operation (quantity, created_at, unit_price, type, product_id)
            VALUES (?, ?, ?, ?, ?)
            """;

        String sqlUpdateProduct = """
            UPDATE product
            SET quantity = quantity + ?
            WHERE id = ?
            """;

        // pour une vente, on mettra quantity négatif pour la mise à jour du produit
        int delta = operation.getType() == StockOperationType.BUY
                ? operation.getQuantity()
                : -operation.getQuantity();

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psOp = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psProd = conn.prepareStatement(sqlUpdateProduct)) {

                // INSERT stock_operation
                psOp.setInt(1, operation.getQuantity());
                psOp.setTimestamp(2, Timestamp.valueOf(operation.getCreatedAt()));
                psOp.setDouble(3, operation.getUnitPrice());
                psOp.setString(4, operation.getType().name()); // "BUY" / "SELL"
                psOp.setInt(5, operation.getProduct().getId());
                psOp.executeUpdate();

                try (ResultSet rs = psOp.getGeneratedKeys()) {
                    if (rs.next()) {
                        operation.setId(rs.getInt(1));
                    }
                }

                // UPDATE product.quantity
                psProd.setInt(1, delta);
                psProd.setInt(2, operation.getProduct().getId());
                psProd.executeUpdate();

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<StockOperation> findByProduct(Product product) {
        List<StockOperation> result = new ArrayList<>();

        String sql = """
            SELECT id, quantity, created_at, unit_price, type
            FROM stock_operation
            WHERE product_id = ?
            ORDER BY created_at DESC
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, product.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs, product));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public double getTotalIncome() {
        String sql = """
            SELECT COALESCE(SUM(quantity * unit_price), 0) AS total_income
            FROM stock_operation
            WHERE type = 'sell'
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total_income");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    @Override
    public double getTotalCost() {
        String sql = """
            SELECT COALESCE(SUM(quantity * unit_price), 0) AS total_cost
            FROM stock_operation
            WHERE type = 'buy'
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total_cost");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    private StockOperation mapRow(ResultSet rs, Product product) throws SQLException {
        StockOperation op = new StockOperation();
        op.setId(rs.getInt("id"));
        op.setQuantity(rs.getInt("quantity"));
        Timestamp ts = rs.getTimestamp("created_at");
        op.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        op.setUnitPrice(rs.getDouble("unit_price"));

        String typeStr = rs.getString("type"); // "buy" / "sell"
        op.setType(StockOperationType.valueOf(typeStr.toUpperCase()));

        op.setProduct(product);
        return op;
    }
}
