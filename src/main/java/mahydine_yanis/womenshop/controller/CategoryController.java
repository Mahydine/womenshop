package mahydine_yanis.womenshop.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.cell.CheckBoxTableCell;
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
    private TableColumn<Category, Double> discountRateColumn;

    @FXML
    private TableColumn<Category, Boolean> activeDiscountColumn;


    @FXML
    private TextField nameField;

    @FXML
    private TextField discountField;

    private final CategoryDao categoryDAO = new CategoryDaoImpl();
    private final ObservableList<Category> categoryList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configure les colonnes
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        // colonne discount : affichage en %
        discountRateColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getDiscountRate() * 100).asObject());

        // colonne promotion active : checkbox dans la ligne
        activeDiscountColumn.setCellValueFactory(cellData -> {
            Category cat = cellData.getValue();
            SimpleBooleanProperty prop = new SimpleBooleanProperty(cat.isActiveDiscount());

            // quand on clique sur la checkbox, on met à jour l'objet + la BDD
            prop.addListener((obs, oldVal, newVal) -> {
                cat.setActiveDiscount(newVal);
                categoryDAO.update(cat);
            });

            return prop;
        });

        activeDiscountColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activeDiscountColumn));

        // rendre la table éditable (nécessaire pour CheckBoxTableCell)
        categoryTable.setEditable(true);

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
        String discountText = discountField.getText().trim();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Nom vide", "Veuillez saisir un nom de catégorie.");
            return;
        }

        double discountRate = 0.0; // par défaut 0%
        if (!discountText.isEmpty()) {
            try {
                double percent = Double.parseDouble(discountText.replace(',', '.'));
                if (percent < 0 || percent > 100) {
                    showAlert(Alert.AlertType.WARNING,
                            "Réduction invalide",
                            "La réduction doit être comprise entre 0 et 100.");
                    return;
                }
                discountRate = percent / 100.0; // stocké en 0.0–1.0 en base
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR,
                        "Format invalide",
                        "La réduction doit être un nombre (ex : 10 ou 25.5).");
                return;
            }
        }

        Category c = new Category();
        c.setName(name);
        c.setDiscountRate(discountRate);
        c.setActiveDiscount(false); // par défaut non active

        categoryDAO.save(c);   // insère en BDD + setId
        categoryList.add(c);   // ajoute à la liste affichée

        nameField.clear();
        discountField.clear();
    }


    @FXML
    private void onDeleteSelected() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner une catégorie à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Suppression de la catégorie");
        confirm.setContentText("Supprimer la catégorie : " + selected.getName() + " ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean deleted = categoryDAO.delete(selected.getId());
                if (!deleted) {
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Suppression impossible",
                            "Cette catégorie est utilisée par au moins un produit.\n" +
                                    "Vous devez d'abord modifier ou supprimer les produits associés."
                    );
                    return;
                }
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
