package mahydine_yanis.womenshop.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mahydine_yanis.womenshop.HelloApplication;
import mahydine_yanis.womenshop.dao.CategoryDao;
import mahydine_yanis.womenshop.dao.ProductDao;
import mahydine_yanis.womenshop.daoimpl.CategoryDaoImpl;
import mahydine_yanis.womenshop.daoimpl.ProductDaoImpl;
import mahydine_yanis.womenshop.model.Category;
import mahydine_yanis.womenshop.model.Product;
import javafx.scene.control.TextInputDialog;
import mahydine_yanis.womenshop.service.FinanceService;

import java.time.LocalDateTime;
import java.util.Optional;

import mahydine_yanis.womenshop.dao.StockOperationDao;
import mahydine_yanis.womenshop.daoimpl.StockOperationDaoImpl;
import mahydine_yanis.womenshop.model.StockOperation;
import mahydine_yanis.womenshop.util.StockOperationType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductController {

    @FXML
    private ComboBox<Category> categoryFilter;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TableColumn<Product, Double> sellPriceColumn;

    @FXML
    private TableColumn<Product, Double> purchasePriceColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, Double> discountRateColumn;

    @FXML
    private TableColumn<Product, String> discountActiveColumn;

    private final ProductDao productDao = new ProductDaoImpl();
    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    private final CategoryDao categoryDao = new CategoryDaoImpl();
    private final ObservableList<Category> categoriesList = FXCollections.observableArrayList();

    private final StockOperationDao stockOperationDao = new StockOperationDaoImpl();

    private final FinanceService financeService = new FinanceService();


    @FXML
    private void initialize() {

        // Initialisation de la table ---------------
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        sellPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        purchasePriceColumn.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        // ces colonnes ont des données qui sont dans l'attribut "Category category" d'un produit
        discountRateColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCategory().getDiscountRate()).asObject());
        discountActiveColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().isActiveDiscount() ? "Yes" : "No"));

        loadProducts();
        loadCategories();

        // écouteur sur le filtre
        categoryFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldCat, newCat) -> {
            if (newCat != null) {
                loadProductsByCategory(newCat);
            }
        });
    }

    private void loadProducts() {
        productList.clear();
        productList.addAll(productDao.getAll());
        productTable.setItems(productList);
    }

    private void loadProductsByCategory(Category category) {
        productList.clear();
        productList.addAll(productDao.findByCategory(category));
        productTable.setItems(productList);
    }

    private void loadCategories() {
        categoriesList.clear();
        categoriesList.addAll(categoryDao.getAll());
        categoryFilter.setItems(categoriesList);
    }

    @FXML
    private void onClearFilter() {
        categoryFilter.getSelectionModel().clearSelection();
        productTable.getSortOrder().clear();
        loadProducts();
    }

    @FXML
    private void onSortPriceAsc() {
        sellPriceColumn.setSortType(TableColumn.SortType.ASCENDING);
        productTable.getSortOrder().clear();
        productTable.getSortOrder().add(sellPriceColumn);
    }

    @FXML
    private void onSortPriceDesc() {
        sellPriceColumn.setSortType(TableColumn.SortType.DESCENDING);
        productTable.getSortOrder().clear();
        productTable.getSortOrder().add(sellPriceColumn);
    }

    @FXML
    private void onAddProduct() {
        Product created = openProductDialog(null, "Add product");
        if (created != null) {
            productDao.save(created);
            productList.add(created);
        }
    }

    @FXML
    private void onEditProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "No selection", "Please select a product to edit.");
            return;
        }

        Product updated = openProductDialog(selected, "Edit product");
        if (updated != null) {
            productDao.update(updated);
            productTable.refresh();
        }
    }

    @FXML
    private void onDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "No selection", "Please select a product to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete product");
        confirm.setHeaderText("Delete product");
        confirm.setContentText("Are you sure you want to delete product: " + selected.getName() + " ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean deleted = productDao.delete(selected.getId());
                if (deleted) {
                    productList.remove(selected);   // ou refreshTable();
                } else {
                    showAlert(Alert.AlertType.ERROR,
                            "Delete failed",
                            "Unable to delete this product.");
                }
            }
        });
    }


    private Product openProductDialog(Product product, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("popup/product-form-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(productTable.getScene().getWindow());
            dialogStage.setScene(scene);

            ProductFormController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTitle(title);
            controller.setCategories(categoryDao.getAll());
            if (product == null) {
                controller.setProduct(null);
            } else {
                controller.setProduct(product);
            }

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                return controller.getProduct();
            }
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open product form dialog.");
            return null;
        }
    }

    @FXML
    private void onBuyProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "No selection", "Please select a product to buy.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buy items");
        dialog.setHeaderText("Buy items for product: " + selected.getName());
        dialog.setContentText("Quantity to buy:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return; // annulé
        }

        int quantity;
        try {
            quantity = Integer.parseInt(result.get().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number", "Quantity must be an integer.");
            return;
        }

        if (quantity <= 0) {
            showAlert(Alert.AlertType.ERROR, "Invalid quantity", "Quantity must be > 0.");
            return;
        }

        // Vérifier le capital disponible avant d'acheter
        double currentCapital = financeService.getCurrentCapital();
        double purchaseCost = quantity * selected.getPurchasePrice();

        if (purchaseCost > currentCapital) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Not enough capital",
                    "You do not have enough capital to buy " + quantity +
                            " items of " + selected.getName() +
                            ".\nRequired: " + String.format("%.2f", purchaseCost) +
                            " | Available: " + String.format("%.2f", currentCapital)
            );
            return;
        }


        // Création de l'opération d'achat
        StockOperation op = new StockOperation();
        op.setProduct(selected);
        op.setQuantity(quantity);
        op.setCreatedAt(LocalDateTime.now());
        op.setUnitPrice(selected.getPurchasePrice()); // prix d'achat fixe
        op.setType(StockOperationType.BUY);

        stockOperationDao.save(op);

        // Met à jour le stock côté objet + vue
        selected.setQuantity(selected.getQuantity() + quantity);
        productTable.refresh();
    }

    @FXML
    private void onSellProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "No selection", "Please select a product to sell.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sell items");
        dialog.setHeaderText("Sell items for product: " + selected.getName());
        dialog.setContentText("Quantity to sell:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return; // annulé
        }

        int quantity;
        try {
            quantity = Integer.parseInt(result.get().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number", "Quantity must be an integer.");
            return;
        }

        if (quantity <= 0) {
            showAlert(Alert.AlertType.ERROR, "Invalid quantity", "Quantity must be > 0.");
            return;
        }

        if (quantity > selected.getQuantity()) {
            showAlert(Alert.AlertType.ERROR, "Not enough stock",
                    "You cannot sell more items than available in stock.");
            return;
        }

        double effectiveSellPrice = getEffectiveSellPrice(selected);

        // Création de l'opération de vente
        StockOperation op = new StockOperation();
        op.setProduct(selected);
        op.setQuantity(quantity);
        op.setCreatedAt(LocalDateTime.now());
        op.setUnitPrice(effectiveSellPrice); // prix de vente (avec remise éventuelle)
        op.setType(StockOperationType.SELL);

        stockOperationDao.save(op);

        // Met à jour le stock côté objet + vue
        selected.setQuantity(selected.getQuantity() - quantity);
        productTable.refresh();
    }


    private double getEffectiveSellPrice(Product product) {
        double base = product.getEffectivePrice();
        Category cat = product.getCategory();

        if (cat != null && cat.isActiveDiscount() && product.getDiscountPrice() == 0) {
            double rate = cat.getDiscountRate(); // ex: 0.30 pour 30 %
            base = product.getSalePrice() * (1.0 - rate);
        }
        return base;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
