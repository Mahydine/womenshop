package mahydine_yanis.womenshop.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mahydine_yanis.womenshop.dao.StockOperationDao;
import mahydine_yanis.womenshop.daoimpl.StockOperationDaoImpl;
import mahydine_yanis.womenshop.model.StockOperation;
import mahydine_yanis.womenshop.util.StockOperationType;
import mahydine_yanis.womenshop.service.FinanceService;

import java.time.format.DateTimeFormatter;

public class StatsController {

    @FXML
    private Label capitalLabel;

    @FXML
    private Label incomeLabel;

    @FXML
    private Label costLabel;

    @FXML
    private TableView<OperationRow> operationTable;

    @FXML
    private TableColumn<OperationRow, String> dateColumn;

    @FXML
    private TableColumn<OperationRow, String> typeColumn;

    @FXML
    private TableColumn<OperationRow, String> productColumn;

    @FXML
    private TableColumn<OperationRow, Integer> quantityColumn;

    @FXML
    private TableColumn<OperationRow, Double> unitPriceColumn;

    @FXML
    private TableColumn<OperationRow, Double> amountColumn;

    @FXML
    private TableColumn<OperationRow, Double> balanceBeforeColumn;

    @FXML
    private TableColumn<OperationRow, Double> balanceAfterColumn;

    private final FinanceService financeService = new FinanceService();
    private final StockOperationDao stockOperationDao = new StockOperationDaoImpl();

    private final ObservableList<OperationRow> operationList = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private void initialize() {
        updateFinanceInfo();
        setupOperationTable();
        loadOperations();
    }

    private void updateFinanceInfo() {
        double totalIncome = financeService.getTotalIncome();
        double totalCost = financeService.getTotalCost();
        double capital = financeService.getCurrentCapital();

        incomeLabel.setText(String.format("%.2f", totalIncome));
        costLabel.setText(String.format("%.2f", totalCost));
        capitalLabel.setText(String.format("%.2f", capital));
    }

    private void setupOperationTable() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        balanceBeforeColumn.setCellValueFactory(new PropertyValueFactory<>("balanceBefore"));
        balanceAfterColumn.setCellValueFactory(new PropertyValueFactory<>("balanceAfter"));

        operationTable.setItems(operationList);
    }

    private void loadOperations() {
        operationList.clear();

        double balance = financeService.getInitialCapital();

        for (StockOperation op : stockOperationDao.getAllOrderedByDate()) {
            double rawAmount = op.getQuantity() * op.getUnitPrice();
            // BUY diminue le capital, SELL l'augmente
            double amount = (op.getType() == StockOperationType.BUY) ? -rawAmount : rawAmount;

            double before = balance;
            double after = balance + amount;

            OperationRow row = new OperationRow(
                    op.getCreatedAt().format(DATE_TIME_FMT),
                    op.getType().name(),                     // "BUY" / "SELL"
                    op.getProduct().getName(),
                    op.getQuantity(),
                    op.getUnitPrice(),
                    amount,
                    before,
                    after
            );

            operationList.add(row);
            balance = after;
        }
    }

    public static class OperationRow {
        private final String date;
        private final String type;
        private final String product;
        private final int quantity;
        private final double unitPrice;
        private final double amount;
        private final double balanceBefore;
        private final double balanceAfter;

        public OperationRow(String date,
                            String type,
                            String product,
                            int quantity,
                            double unitPrice,
                            double amount,
                            double balanceBefore,
                            double balanceAfter) {
            this.date = date;
            this.type = type;
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.amount = amount;
            this.balanceBefore = balanceBefore;
            this.balanceAfter = balanceAfter;
        }

        public String getDate() {
            return date;
        }

        public String getType() {
            return type;
        }

        public String getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getAmount() {
            return amount;
        }

        public double getBalanceBefore() {
            return balanceBefore;
        }

        public double getBalanceAfter() {
            return balanceAfter;
        }
    }
}
