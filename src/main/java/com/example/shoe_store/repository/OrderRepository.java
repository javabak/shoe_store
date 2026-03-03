package com.example.shoe_store.repository;

import com.example.shoe_store.config.DatabaseConnection;
import com.example.shoe_store.model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class OrderRepository {

    private final ObservableList<Order> observableOrders = FXCollections.observableArrayList();

    public ObservableList<Order> getObservableOrders() {
        return observableOrders;
    }

    public void refresh() {
        observableOrders.clear();
        String query = "SELECT o.order_id, o.article_number, u.full_name, s.status_name, " +
                       "p.address, o.pickup_code, o.order_date, o.pickup_date " +
                       "FROM orders o " +
                       "JOIN users u ON o.user_id = u.user_id " +
                       "JOIN order_statuses s ON o.status_id = s.status_id " +
                       "JOIN pickup_points p ON o.pickup_point_id = p.pickup_point_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                observableOrders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getString("article_number"),
                        rs.getString("full_name"),
                        rs.getString("status_name"),
                        rs.getString("address"),
                        rs.getString("pickup_code"),
                        rs.getObject("order_date", LocalDate.class),
                        rs.getObject("pickup_date", LocalDate.class)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertOrder(Order order, int currentUserId) {
        String sql = "INSERT INTO orders" +
                     " (article_number, user_id, status_id, pickup_point_id, order_date, pickup_date, pickup_code) " +
                     "VALUES (?, ?, " +
                     "(SELECT status_id FROM order_statuses WHERE status_name = ?), " +
                     "(SELECT pickup_point_id FROM pickup_points WHERE address = ?), " +
                     "?, ?, ?)";

        String pickupCode = order.getPickupCode();
        if (pickupCode == null || pickupCode.isEmpty()) {
            pickupCode = (char)('A' + (int)(Math.random() * 26)) + String.valueOf((int)(Math.random() * 900) + 100);
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, order.getArticleNumber());
            pstmt.setInt(2, currentUserId);
            pstmt.setString(3, order.getStatus());
            pstmt.setString(4, order.getPickupPoint());
            pstmt.setObject(5, order.getOrderDate());
            pstmt.setObject(6, order.getPickupDate());
            pstmt.setString(7, pickupCode);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
            refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrder(Order order) {
        String sql = "UPDATE orders SET status_id = (SELECT status_id FROM order_statuses WHERE status_name = ?), " +
                     "pickup_point_id = (SELECT pickup_point_id FROM pickup_points WHERE address = ?), " +
                     "pickup_date = ? WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, order.getStatus());
            pstmt.setString(2, order.getPickupPoint());
            pstmt.setObject(3, order.getPickupDate());
            pstmt.setInt(4, order.getOrderId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<String> getAllStatuses() {
        ObservableList<String> statuses = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT status_name FROM order_statuses")) {
            while (rs.next()) statuses.add(rs.getString("status_name"));
        } catch (SQLException e) { e.printStackTrace(); }
        return statuses;
    }

    public ObservableList<String> getAllPickupPoints() {
        ObservableList<String> points = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT address FROM pickup_points")) {
            while (rs.next()) points.add(rs.getString("address"));
        } catch (SQLException e) { e.printStackTrace(); }
        return points;
    }
}