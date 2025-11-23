module mahydine_yanis.womenshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens mahydine_yanis.womenshop to javafx.fxml;
    exports mahydine_yanis.womenshop;
    exports mahydine_yanis.womenshop.controller;
    exports mahydine_yanis.womenshop.model;
    opens mahydine_yanis.womenshop.controller to javafx.fxml;
    opens mahydine_yanis.womenshop.model to javafx.base;
}