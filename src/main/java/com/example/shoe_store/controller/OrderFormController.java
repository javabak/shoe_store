package com.example.shoe_store.controller;

import com.example.shoe_store.model.Order;
import com.example.shoe_store.repository.OrderRepository;
import com.example.shoe_store.service.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class OrderFormController {

    @FXML private TextField idField;
    @FXML private ComboBox<String> statusBox;
    @FXML private ComboBox<String> addressBox;
    @FXML private DatePicker orderDatePicker;
    @FXML private DatePicker pickupDatePicker;
    @FXML private Label formTitle;

    private final OrderRepository repository = new OrderRepository();
    private Order currentOrder;

    @FXML
    public void initialize() {
        statusBox.setItems(repository.getAllStatuses());
        addressBox.setItems(repository.getAllPickupPoints());
    }

    public void setOrder(Order order) {
        this.currentOrder = order;

        if (order != null) {
            formTitle.setText("Редактирование заказа");
            idField.setText(order.getArticleNumber());
            statusBox.getSelectionModel().select(order.getStatus());
            addressBox.getSelectionModel().select(order.getPickupPoint());
            orderDatePicker.setValue(order.getOrderDate());
            pickupDatePicker.setValue(order.getPickupDate());
        } else {
            formTitle.setText("Новый заказ");
            idField.setPromptText("Введите уникальный артикул");
            orderDatePicker.setValue(LocalDate.now()); // По умолчанию сегодня
        }
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            if (currentOrder == null) {
                Order newOrder = new Order();
                newOrder.setArticleNumber(idField.getText());
                newOrder.setStatus(statusBox.getValue());
                newOrder.setPickupPoint(addressBox.getValue());
                newOrder.setOrderDate(orderDatePicker.getValue());
                newOrder.setPickupDate(pickupDatePicker.getValue());

                int adminId = Session.getCurrentUser().userId();

                repository.insertOrder(newOrder, adminId);
            } else {
                // Обновляем существующий
                currentOrder.setArticleNumber(idField.getText());
                currentOrder.setStatus(statusBox.getValue());
                currentOrder.setPickupPoint(addressBox.getValue());
                currentOrder.setPickupDate(pickupDatePicker.getValue());

                repository.updateOrder(currentOrder);
            }
            closeWindow();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (idField.getText() == null || idField.getText().isEmpty()) {
            errorMessage += "Не указан артикул заказа!\n";
        }
        if (statusBox.getValue() == null) {
            errorMessage += "Выберите статус заказа!\n";
        }
        if (addressBox.getValue() == null) {
            errorMessage += "Выберите пункт выдачи!\n";
        }
        if (orderDatePicker.getValue() == null) {
            errorMessage += "Укажите дату заказа!\n";
        }

        if (orderDatePicker.getValue() != null && pickupDatePicker.getValue() != null) {
            if (pickupDatePicker.getValue().isBefore(orderDatePicker.getValue())) {
                errorMessage += "Дата выдачи не может быть раньше даты заказа!\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка заполнения");
            alert.setHeaderText("Пожалуйста, исправьте следующие поля:");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    @FXML
    private void handleBack() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}