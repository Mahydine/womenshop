package mahydine_yanis.womenshop.service;

import mahydine_yanis.womenshop.dao.StockOperationDao;
import mahydine_yanis.womenshop.daoimpl.StockOperationDaoImpl;

public class FinanceService {

    private static final double INITIAL_CAPITAL = 10_000.0; // valeur unique

    private final StockOperationDao stockOperationDao = new StockOperationDaoImpl();

    public double getInitialCapital() {
        return INITIAL_CAPITAL;
    }

    public double getTotalIncome() {
        return stockOperationDao.getTotalIncome();
    }

    public double getTotalCost() {
        return stockOperationDao.getTotalCost();
    }

    public double getCurrentCapital() {
        return INITIAL_CAPITAL + getTotalIncome() - getTotalCost();
    }
}
