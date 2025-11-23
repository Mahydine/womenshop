package mahydine_yanis.womenshop.dao;

import mahydine_yanis.womenshop.model.Product;
import mahydine_yanis.womenshop.model.StockOperation;

import java.util.List;

public interface StockOperationDao {

    void save(StockOperation operation);

    List<StockOperation> findByProduct(Product product);

    double getTotalIncome(); // somme des ventes (sell)

    double getTotalCost();   // somme des achats (buy)

    List<StockOperation> getAllOrderedByDate();
}
