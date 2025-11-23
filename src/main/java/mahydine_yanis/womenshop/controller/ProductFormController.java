package mahydine_yanis.womenshop.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mahydine_yanis.womenshop.model.Category;
import mahydine_yanis.womenshop.model.Product;

public class ProductFormController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<Category> categoryCombo;

    @FXML
    private TextField purchasePriceField;

    @FXML
    private TextField sellPriceField;

    private final ObservableList<Category> categoryList = FXCollections.observableArrayList();

    private Stage dialogStage;
    private Product product;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        categoryCombo.setItems(categoryList);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /** Mode ajout ou édition */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /** Liste des catégories à afficher dans la combo */
    public void setCategories(Iterable<Category> categories) {
        categoryList.clear();
        for (Category c : categories) {
            categoryList.add(c);
        }
    }

    /** Produit à éditer (ou nouvel objet pour ajout) */
    public void setProduct(Product product) {
        this.product = product;

        if (product.getName() != null) {
            nameField.setText(product.getName());
        }
        if (product.getCategory() != null) {
            categoryCombo.getSelectionModel().select(product.getCategory());
        }
        purchasePriceField.setText(String.valueOf(product.getPurchasePrice()));
        sellPriceField.setText(String.valueOf(product.getSellPrice()));
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public Product getProduct() {
        return product;
    }

    @FXML
    private void onSave() {
        if (!validate()) {
            return;
        }

        String name = nameField.getText().trim();
        double purchasePrice = Double.parseDouble(purchasePriceField.getText().trim());
        double sellPrice = Double.parseDouble(sellPriceField.getText().trim());
        Category category = categoryCombo.getSelectionModel().getSelectedItem();

        product.setName(name);
        product.setPurchasePrice(purchasePrice);
        product.setSellPrice(sellPrice);
        product.setCategory(category);
        product.setActive(true);

        okClicked = true;
        dialogStage.close();
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }

    private boolean validate() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errors.append("- Name is required.\n");
        }
        if (categoryCombo.getSelectionModel().getSelectedItem() == null) {
            errors.append("- Category is required.\n");
        }
        try {
            double p = Double.parseDouble(purchasePriceField.getText().trim());
            if (p <= 0) errors.append("- Purchase price must be > 0.\n");
        } catch (NumberFormatException e) {
            errors.append("- Purchase price must be numeric.\n");
        }
        try {
            double s = Double.parseDouble(sellPriceField.getText().trim());
            if (s <= 0) errors.append("- Sell price must be > 0.\n");
        } catch (NumberFormatException e) {
            errors.append("- Sell price must be numeric.\n");
        }

        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid data");
            alert.setHeaderText("Please correct the following errors:");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
