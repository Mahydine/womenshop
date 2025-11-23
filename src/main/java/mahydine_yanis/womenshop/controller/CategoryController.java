package mahydine_yanis.womenshop.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mahydine_yanis.womenshop.dao.CategoryDao;
import mahydine_yanis.womenshop.daoimpl.CategoryDaoImpl;
import mahydine_yanis.womenshop.model.Category;

public class CategoryController {

    @FXML
    private TableView<Category> categoryTable;

    @FXML
    private TableColumn<Category, Integer> idColumn;

    @FXML
    private TableColumn<Category, String> nameColumn;

    @FXML
    private TextField nameField;

    private final CategoryDao categoryDAO = new CategoryDaoImpl();
    private final ObservableList<Category> categoryList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configure les colonnes
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        // Charge les données depuis la BDD
        refreshTable();
    }

    private void refreshTable() {
        categoryList.clear();
        categoryList.addAll(categoryDAO.getAll());
        categoryTable.setItems(categoryList);
    }

    @FXML
    private void onAddCategory() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Nom vide", "Veuillez saisir un nom de catégorie.");
            return;
        }

        Category c = new Category(name);
        categoryDAO.save(c);   // insère en BDD + récupère l'id
        categoryList.add(c);   // ajoute à la liste affichée

        nameField.clear();
    }

    @FXML
    private void onDeleteSelected() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner une catégorie à supprimer.");
            return;
        }

        // Confirmation simple
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Suppression de la catégorie");
        confirm.setContentText("Supprimer la catégorie : " + selected.getName() + " ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                categoryDAO.delete(selected.getId());
                categoryList.remove(selected);
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
