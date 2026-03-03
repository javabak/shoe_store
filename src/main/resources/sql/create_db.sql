DROP DATABASE IF EXISTS shoe_store;

CREATE DATABASE shoe_store
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE shoe_store;

CREATE TABLE roles (
                       role_id INT PRIMARY KEY AUTO_INCREMENT,
                       role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       login VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(150) NOT NULL,
                       role_id INT NOT NULL,
                       FOREIGN KEY (role_id)
                           REFERENCES roles(role_id)
                           ON DELETE RESTRICT
                           ON UPDATE CASCADE
);

CREATE TABLE categories (
                            category_id INT PRIMARY KEY AUTO_INCREMENT,
                            category_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE manufacturers (
                               manufacturer_id INT PRIMARY KEY AUTO_INCREMENT,
                               manufacturer_name VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE suppliers (
                           supplier_id INT PRIMARY KEY AUTO_INCREMENT,
                           supplier_name VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE units (
                       unit_id INT PRIMARY KEY AUTO_INCREMENT,
                       unit_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE pickup_points (
                               pickup_point_id INT PRIMARY KEY AUTO_INCREMENT,
                               address VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE products (
                          product_id INT PRIMARY KEY AUTO_INCREMENT,
                          article VARCHAR(20) NOT NULL UNIQUE,
                          name VARCHAR(150) NOT NULL,
                          description TEXT,
                          category_id INT NOT NULL,
                          manufacturer_id INT NOT NULL,
                          supplier_id INT NOT NULL,
                          price DECIMAL(10,2) NOT NULL,
                          unit_id INT NOT NULL,
                          stock_quantity INT NOT NULL,
                          image_path VARCHAR(255),
                          discount TINYINT UNSIGNED NOT NULL DEFAULT 0

                          CHECK (price >= 0),
                          CHECK (stock_quantity >= 0),

                          FOREIGN KEY (category_id) REFERENCES categories(category_id),
                          FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(manufacturer_id),
                          FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),
                          FOREIGN KEY (unit_id) REFERENCES units(unit_id)
);

CREATE TABLE order_statuses (
                                status_id INT PRIMARY KEY AUTO_INCREMENT,
                                status_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE orders (
                        order_id INT PRIMARY KEY AUTO_INCREMENT,
                        article_number VARCHAR(50) NOT NULL UNIQUE,
                        user_id INT NOT NULL,
                        status_id INT NOT NULL,
                        pickup_point_id INT NOT NULL,
                        pickup_code VARCHAR(10),
                        order_date DATE NOT NULL,
                        pickup_date DATE,

                        FOREIGN KEY (user_id) REFERENCES users(user_id),
                        FOREIGN KEY (status_id) REFERENCES order_statuses(status_id),
                        FOREIGN KEY (pickup_point_id) REFERENCES pickup_points(pickup_point_id)
);

CREATE TABLE order_items (
                             order_item_id INT PRIMARY KEY AUTO_INCREMENT,
                             order_id INT NOT NULL,
                             product_id INT NOT NULL,
                             quantity INT NOT NULL,
                             CHECK (quantity > 0),

                             FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT
);