package com.example.shoe_store;

import com.example.shoe_store.controller.OrderFormController;
import com.example.shoe_store.controller.ProductFormController;
import com.example.shoe_store.model.Order;
import com.example.shoe_store.model.Product;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {

        primaryStage = stage;

        stage.getIcons().add(
                new Image(Objects.requireNonNull(getClass().getResource("/images/Icon.png")).toExternalForm())
        );

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/fxml/login.fxml"));

        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        stage.setTitle("Система учета обуви");
        stage.setScene(scene);
        stage.show();
    }

        public static void switchScene(String fxml) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(Main.class.getResource("/fxml/" + fxml));
            Parent root = loader.load();

            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openProductForm(Product product) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/product-form.fxml"));
        Parent root = loader.load();

        // Получаем контроллер, чтобы передать в него объект товара
        ProductFormController controller = loader.getController();
        controller.setProduct(product);

        Stage stage = new Stage();
        stage.setTitle(product == null ? "Добавление нового товара" : "Редактирование товара");

        stage.setScene(new Scene(root));

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
    }

    public static void openOrderForm(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/orders-form.fxml"));
            Stage stage = new Stage();
            stage.setTitle(order == null ? "Новый заказ" : "Редактирование заказа");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            // Передаем данные в контроллер формы
            OrderFormController controller = loader.getController();
            controller.setOrder(order);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        launch();
    }
}