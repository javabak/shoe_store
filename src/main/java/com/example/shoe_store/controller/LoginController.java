package com.example.shoe_store.controller;

import com.example.shoe_store.Main;
import com.example.shoe_store.config.DatabaseConnection;
import com.example.shoe_store.repository.UserRepository;
import com.example.shoe_store.service.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.example.shoe_store.model.User;

import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private final UserRepository repository;

    {
        try {
            repository = new UserRepository(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка валидации", "Пустые поля", "Пожалуйста, введите логин и пароль.");
            return;
        }

        Platform.runLater(() ->
                loginButton.getScene().getRoot().requestFocus());

        User user = repository.findByLoginAndPassword(login, password);

        if (user != null) {
            Session.setCurrentUser(user);
            Main.switchScene("products.fxml");
        } else {
            showAlert("Ошибка авторизации", "Неверные данные", "Пользователь с таким логином и паролем не найден.");
        }

    }

    @FXML
    private void handleGuest() {
        Session.setCurrentUser(null);
        Main.switchScene("products.fxml");
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
