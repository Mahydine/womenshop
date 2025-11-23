package mahydine_yanis.womenshop.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mahydine_yanis.womenshop.dao.CategoryDao;
import mahydine_yanis.womenshop.dao.ProductDao;
import mahydine_yanis.womenshop.daoimpl.CategoryDaoImpl;
import mahydine_yanis.womenshop.daoimpl.ProductDaoImpl;
import mahydine_yanis.womenshop.model.Category;
import mahydine_yanis.womenshop.model.Product;

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
        categoryFilter.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldCat, newCat) -> {
                    if (newCat != null) {
                        loadProductsByCategory(newCat);
                    }
                }
        );
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

    private void loadCategories(){
        categoriesList.clear();
        categoriesList.addAll(categoryDao.getAll());
        categoryFilter.setItems(categoriesList);
    }

    @FXML
    private void onClearFilter(){
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


}
