package com.example.shoe_store.repository;

import com.example.shoe_store.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRepository {

    private final Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    public User findByLoginAndPassword(String login, String password) {

        String sql = """
                SELECT u.user_id,
                       u.full_name,
                       u.login,
                       u.password_hash,
                       r.role_name
                FROM users u
                JOIN roles r ON u.role_id = r.role_id
                WHERE u.login = ? AND u.password_hash = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, login);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("login"),
                        resultSet.getString("password_hash"),
                        resultSet.getString("role_name")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}