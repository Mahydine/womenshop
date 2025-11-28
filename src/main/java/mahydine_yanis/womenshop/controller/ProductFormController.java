package mahydine_yanis.womenshop.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mahydine_yanis.womenshop.model.*;

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

    @FXML
    private TextField sizeField;

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

        if (product != null && product.getName() != null) {
            nameField.setText(product.getName());
        }
        if (product != null && product.getCategory() != null) {
            categoryCombo.getSelectionModel().select(product.getCategory());
        }
        if (product != null) {
            purchasePriceField.setText(String.valueOf(product.getPurchasePrice()));
            sellPriceField.setText(String.valueOf(product.getSalePrice()));
            sellPriceField.setDisable(true); // Sale price is fixed and cannot be modified after creation

            if (product instanceof Clothing clothing) {
                sizeField.setText(String.valueOf(clothing.getSize()));
            } else if (product instanceof Shoes shoes) {
                sizeField.setText(String.valueOf(shoes.getSize()));
            }
        }
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

        Product preparedProduct = ensureProductMatchesCategory(category);
        preparedProduct.setName(name);
        preparedProduct.setPurchasePrice(purchasePrice);
        preparedProduct.setSellPrice(sellPrice);
        preparedProduct.setCategory(category);
        preparedProduct.setActive(true);

        if (preparedProduct instanceof Clothing clothing) {
            clothing.setSize(Integer.parseInt(sizeField.getText().trim()));
        } else if (preparedProduct instanceof Shoes shoes) {
            shoes.setSize(Integer.parseInt(sizeField.getText().trim()));
        }

        this.product = preparedProduct;

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
        Double purchasePrice = parseDouble(purchasePriceField.getText().trim(), "Purchase price", errors);
        Double sellPrice = parseDouble(sellPriceField.getText().trim(), "Sell price", errors);

        if (purchasePrice != null && sellPrice != null && purchasePrice > sellPrice) {
            errors.append("- Purchase price cannot be greater than sale price.\n");
        }

        Category category = categoryCombo.getSelectionModel().getSelectedItem();
        if (category != null && needsSizeField(category)) {
            Double parsedSize = parseDouble(sizeField.getText().trim(), "Size", errors);
            if (parsedSize != null) {
                int size = parsedSize.intValue();
                if (isClothing(category) && (size < 34 || size > 54)) {
                    errors.append("- Clothing size must be between 34 and 54.\n");
                }
                if (isShoes(category) && (size < 36 || size > 50)) {
                    errors.append("- Shoes size must be between 36 and 50.\n");
                }
            }
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

    private Double parseDouble(String value, String fieldName, StringBuilder errors) {
        try {
            double parsed = Double.parseDouble(value);
            if (parsed < 0) {
                errors.append("- ").append(fieldName).append(" cannot be negative.\n");
                return null;
            }
            return parsed;
        } catch (NumberFormatException e) {
            errors.append("- ").append(fieldName).append(" must be numeric.\n");
            return null;
        }
    }

    private Product ensureProductMatchesCategory(Category category) {
        if (product == null) {
            return instantiateByCategory(category);
        }

        // Prevent type changes during edit
        if (product instanceof Clothing && !isClothing(category)) {
            throw new IllegalArgumentException("Cannot change product type from Clothing");
        }
        if (product instanceof Shoes && !isShoes(category)) {
            throw new IllegalArgumentException("Cannot change product type from Shoes");
        }
        if (product instanceof Accessory && (isClothing(category) || isShoes(category))) {
            throw new IllegalArgumentException("Cannot change product type from Accessory");
        }
        return product;
    }

    private Product instantiateByCategory(Category category) {
        if (isClothing(category)) {
            return new Clothing();
        }
        if (isShoes(category)) {
            return new Shoes();
        }
        return new Accessory();
    }

    private boolean isClothing(Category category) {
        return category != null && category.getName() != null && category.getName().equalsIgnoreCase("Clothing");
    }

    private boolean isShoes(Category category) {
        return category != null && category.getName() != null && category.getName().equalsIgnoreCase("Shoes");
    }

    private boolean needsSizeField(Category category) {
        return isClothing(category) || isShoes(category);
    }
}
