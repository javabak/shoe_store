package com.example.shoe_store.controller;

import com.example.shoe_store.Main;
import com.example.shoe_store.model.Order;
import com.example.shoe_store.model.User;
import com.example.shoe_store.repository.OrderRepository;
import com.example.shoe_store.service.Session;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.example.shoe_store.controller.ProductController.showAlert;

public class OrderController {

    @FXML private TableView<Order> orderTable;
    @FXML private Button addButton;
    @FXML private Button deleteButton;

    private final OrderRepository repository = new OrderRepository();

    @FXML
    public void initialize() {
        repository.refresh();
        orderTable.setItems(repository.getObservableOrders());

        setupOrderTable();
        applyPermissions();

        // Редактирование при двойном клике (только для админа)
        orderTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            Node header = orderTable.lookup("TableHeaderRow");
            if (header != null) {
                header.setManaged(false);
                header.setVisible(false);
            }
        });

        Platform.runLater(() -> {
            orderTable.getSelectionModel().clearSelection();
            addButton.requestFocus();
        });

        Platform.runLater(() -> orderTable.requestFocus());


    }

    private void setupOrderTable() {
        orderTable.getColumns().clear();
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        orderTable.setFocusTraversable(false);
        TableColumn<Order, Order> column = new TableColumn<>("Список заказов");
        column.setSortable(false);
        column.setReorderable(false);

        column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);
                if (order == null || empty) {
                    setGraphic(null);
                    return;
                }

                // ЛЕВЫЙ БЛОК: Артикул, Статус, Адрес, Дата заказа
                Label article = new Label("Артикул заказа: " + order.getArticleNumber());
                article.setStyle("-fx-font-weight: bold; -fx-font-family: 'Times New Roman';");

                VBox infoBox = new VBox(5,
                        article,
                        new Label("Статус заказа: " + order.getStatus()),
                        new Label("Адрес пункта выдачи: " + order.getPickupPoint()),
                        new Label("Дата заказа: " + order.getOrderDate())
                );
                infoBox.setStyle("-fx-font-family: 'Times New Roman';");
                HBox.setHgrow(infoBox, Priority.ALWAYS);

                // ПРАВЫЙ БЛОК: Дата доставки
                Label dateHeader = new Label("Дата доставки");
                dateHeader.setStyle("-fx-font-weight: bold;");
                Label deliveryDate = new Label(order.getPickupDate() != null ?
                        order.getPickupDate().toString() : "Ожидается");

                VBox datePane = new VBox(5, dateHeader, deliveryDate);
                datePane.setAlignment(Pos.CENTER);
                datePane.setMinWidth(150);
                datePane.setPrefWidth(150);
                datePane.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 1; -fx-font-family: 'Times New Roman';");

                HBox root = new HBox(20, infoBox, datePane);
                root.setAlignment(Pos.CENTER_LEFT);
                root.setPadding(new Insets(10, 0, 10, 15));

                setGraphic(root);
            }
        });
        orderTable.getColumns().add(column);
    }

    private void applyPermissions() {
        User user = Session.getCurrentUser();
        boolean isAdmin = user != null && user.isAdmin();
        addButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);
    }

    @FXML
    private void handleAddOrder() {
        Main.openOrderForm(null);
        repository.refresh();
        orderTable.refresh();
    }

    private void handleEditOrder(Order order) {
        Main.openOrderForm(order);
        repository.refresh();
        orderTable.refresh();
    }

    @FXML
    private void handleDeleteOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            showAlert(Alert.AlertType.INFORMATION, "Ошибка", "Заказ не выбран", "Пожалуйста, выберите заказ для удаления.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление заказа №" + selectedOrder.getArticleNumber());
        alert.setContentText("Вы уверены, что хотите безвозвратно удалить этот заказ?");

        alert.getDialogPane().setFocusTraversable(false);

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            repository.deleteOrder(selectedOrder.getOrderId());

            repository.refresh();
            orderTable.setItems(repository.getObservableOrders());

            orderTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void handleBack() {
        Main.switchScene("products.fxml");
    }
}