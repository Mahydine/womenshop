package mahydine_yanis.womenshop.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import mahydine_yanis.womenshop.HelloApplication;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentPane;

    @FXML
    private void initialize() {
        // page par défaut : liste des produits
        showProductList();
    }

    @FXML
    public void showProductList() {
        loadView("product-view.fxml");
    }

    @FXML
    public void showStats() {
        loadView("stats-view.fxml");
    }

    @FXML
    public void showCategory() {
        loadView("category-view.fxml");
    }

    private void loadView(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(fxmlName)
            );
            Node node = loader.load();
            contentPane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
