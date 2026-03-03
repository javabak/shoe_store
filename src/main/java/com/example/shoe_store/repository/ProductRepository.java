package com.example.shoe_store.repository;

import com.example.shoe_store.config.DatabaseConnection;
import com.example.shoe_store.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    private final ObservableList<Product> observableProducts = FXCollections.observableArrayList();

    public ProductRepository() {
        findAll();
    }

    public ObservableList<Product> getObservableProducts() {
        return observableProducts;
    }

    public void refresh() {
        List<Product> freshData = findAll();
        observableProducts.setAll(freshData);
    }


    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();

        String sql = """
            SELECT p.product_id,
                   p.article,
                   p.name,
                   c.category_name,
                   m.manufacturer_name,
                   s.supplier_name,
                   p.description,
                   p.price,
                   p.discount,
                   u.unit_name,
                   p.stock_quantity,
                   p.image_path
            FROM products p
            JOIN categories c ON p.category_id = c.category_id
            JOIN manufacturers m ON p.manufacturer_id = m.manufacturer_id
            JOIN suppliers s ON p.supplier_id = s.supplier_id
            JOIN units u ON p.unit_id = u.unit_id
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("article"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("category_name"),
                        rs.getString("manufacturer_name"),
                        rs.getString("supplier_name"),
                        rs.getDouble("price"),
                        rs.getString("unit_name"),
                        rs.getInt("stock_quantity"),
                        rs.getString("image_path"),
                        rs.getInt("discount")

                ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке товаров: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }



    public void add(Product product) {
        String sql = """
                INSERT INTO products 
                (article, name, description, category_id, 
                 manufacturer_id, supplier_id, price, 
                 unit_id, stock_quantity, image_path, discount) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getArticle());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setInt(4, getIdByName("categories", "category_id", "category_name", product.getCategory()));
            ps.setInt(5, getIdByName("manufacturers", "manufacturer_id", "manufacturer_name", product.getManufacturer()));
            ps.setInt(6, getIdByName("suppliers", "supplier_id", "supplier_name", product.getSupplier()));
            ps.setDouble(7, product.getPrice());
            ps.setInt(8, getIdByName("units", "unit_id", "unit_name", product.getUnit()));
            ps.setInt(9, product.getQuantity());
            ps.setString(10, product.getImagePath());
            ps.setDouble(11, product.getDiscount());

            ps.executeUpdate();
            refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Product product) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT DATABASE(), USER()")) {
            if (rs.next()) {
                System.out.println("Я подключен к базе: " + rs.getString(1));
                System.out.println("Под пользователем: " + rs.getString(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = """
                UPDATE products SET 
                name = ?, 
                description = ?, 
                category_id = ?,
                manufacturer_id = ?,
                supplier_id = ?,
                price = ?, 
                unit_id = ?,
                stock_quantity = ?, 
                image_path = ?,
                discount = ?
                WHERE product_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Ошибка: Соединение с БД не установлено!");
                return;
            }
            conn.setAutoCommit(false);
            try(PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, product.getName());
                ps.setString(2, product.getDescription());
                ps.setInt(3, getIdByName("categories", "category_id", "category_name", product.getCategory()));
                ps.setInt(4, getIdByName("manufacturers", "manufacturer_id", "manufacturer_name", product.getManufacturer()));
                ps.setInt(5, getIdByName("suppliers", "supplier_id", "supplier_name", product.getSupplier()));
                ps.setDouble(6, product.getPrice());
                ps.setInt(7, getIdByName("units", "unit_id", "unit_name", product.getUnit()));
                ps.setInt(8, product.getQuantity());
                ps.setString(9, product.getImagePath());
                ps.setDouble(10, product.getDiscount());
                ps.setInt(11, product.getProductId());


                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    System.out.println("Транзакция завершена (COMMIT). Изменения отправлены!");
                } else {
                    System.err.println("Строка не найдена. Обновлять нечего.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteProduct(int id) {
        String deleteSql = "DELETE FROM products WHERE product_id = ?";
        String resetAiSql = "ALTER TABLE products AUTO_INCREMENT = 1";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();

                Statement st = conn.createStatement();
                st.executeUpdate(resetAiSql);

                conn.commit();
                refresh();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<String> getAllSuppliers() {
        List<String> suppliers = new ArrayList<>();
        String sql = "SELECT supplier_name FROM suppliers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                suppliers.add(rs.getString("supplier_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public boolean isProductInOrders(int productId) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private int getIdByName(String table, String idColumn, String nameColumn, String nameValue) {
        String sql = String.format("SELECT %s FROM %s WHERE %s = ?", idColumn, table, nameColumn);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nameValue);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка в getIdByName для таблицы " + table + ": " + e.getMessage());
        }
        return 1;
    }
}