package com.example.shoe_store.controller;

import com.example.shoe_store.model.Product;
import com.example.shoe_store.repository.ProductRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.*;

public class ProductFormController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private ComboBox<String> manufacturerBox;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextField discountField;
    @FXML private ImageView imageView;
    @FXML private TextField unitField;
    @FXML private TextField supplierField;

    private File selectedImageFile;
    private String oldImagePath;

    private Product product;
    private final ProductRepository repository = new ProductRepository();
    private String currentImagePath = "picture.png";

    @FXML
    public void initialize() {
        categoryBox.getItems().addAll("Кроссовки", "Туфли", "Ботинки", "Сандалии");
        manufacturerBox.getItems().addAll("Nike", "Adidas", "Puma", "Reebok");
    }

    public void setProduct(Product product) {
        this.product = product;

        if (product != null) {
            idField.setText(String.valueOf(product.getProductId()));
            idField.setDisable(false);
            idField.setEditable(false);

            nameField.setText(product.getName());
            descriptionField.setText(product.getDescription());
            priceField.setText(String.valueOf(product.getPrice()));
            quantityField.setText(String.valueOf(product.getQuantity()));
            discountField.setText(String.valueOf(product.getDiscount()));
            unitField.setText(product.getUnit());
            supplierField.setText(product.getSupplier());
            categoryBox.setValue(product.getCategory());
            manufacturerBox.setValue(product.getManufacturer());
            currentImagePath = product.getImagePath();

            oldImagePath = product.getImagePath();

            loadImage(currentImagePath);
        } else {
            idField.setText("Автовычисление");
            idField.setDisable(true);
            loadImage("images/picture.png");
        }
    }

    @FXML
    private void handleSave() {
        StringBuilder errors = new StringBuilder();

        double price = 0;
        int quantity = 0;
        double discount = 0;
        String article = generateRandomArticle();

        try {
            price = Double.parseDouble(priceField.getText().replace(",", "."));
            if (price < 0) errors.append("- Цена не может быть отрицательной.\n");
        } catch (Exception e) { errors.append("- Цена должна быть числом (например, 1500.50).\n"); }

        try {
            quantity = Integer.parseInt(quantityField.getText());
            if (quantity < 0) errors.append("- Количество на складе не может быть отрицательным.\n");
        } catch (Exception e) { errors.append("- Количество должно быть целым числом.\n"); }

        try {
            String discountText = discountField.getText().replace(",", ".");
            discount = (int) Double.parseDouble(discountText);
            if (discount < 0 || discount > 100) errors.append("- Скидка должна быть от 0 до 100.\n");
        } catch (Exception e) {
            errors.append("- Скидка должна быть целым числом.\n");
        }

        if (nameField.getText().isEmpty()) errors.append("- Название товара не может быть пустым.\n");

        if (errors.length() > 0) {
            showErrorAlert("Ошибка заполнения формы", "Пожалуйста, исправьте ошибки:",
                    errors + "\nПорядок действий: проверьте числовые значения и заполните обязательные поля.");
            return;
        }

        try {
            if (product == null) {
                Product newProduct = new Product(0, article, nameField.getText(), descriptionField.getText(),
                        categoryBox.getValue(), manufacturerBox.getValue(), supplierField.getText(),
                        price, unitField.getText(), quantity, currentImagePath, discount);
                repository.add(newProduct);
            } else {
                product.setName(nameField.getText());
                product.setDescription(descriptionField.getText());
                product.setCategory(categoryBox.getValue());
                product.setManufacturer(manufacturerBox.getValue());
                product.setSupplier(supplierField.getText());
                product.setPrice(price);
                product.setUnit(unitField.getText());
                product.setQuantity(quantity);
                product.setDiscount(discount);
                product.setImagePath(currentImagePath);

                saveImageIfChanged();
                repository.update(product);
            }
            closeWindow();
        } catch (Exception e) {
            showErrorAlert("Системная ошибка", "Не удалось сохранить данные", "Проверьте подключение к базе данных.");
        }
    }

    @FXML
    private void handleUploadImage() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(null);

        if (file != null) {
            try {
                Path rootImagesDir = Paths.get("images");
                if (!Files.exists(rootImagesDir)) Files.createDirectories(rootImagesDir);

                // 1. Удаляем старое физическое фото
                if (currentImagePath != null && !currentImagePath.equals("picture.png")) {
                    Files.deleteIfExists(rootImagesDir.resolve(currentImagePath));
                }

                // 2. Генерируем имя и сохраняем в корень/images
                String newFileName = System.currentTimeMillis() + "_" + file.getName();
                Path dest = rootImagesDir.resolve(newFileName);
                Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

                // 3. Обновляем переменные
                currentImagePath = newFileName;
                loadImage(currentImagePath);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void loadImage(String path) {
        try {
            File file = new File("images/" + path);

            if (file.exists()) {
                // Превращаем путь к файлу в URI, чтобы JavaFX мог его прочитать
                Image img = new Image(file.toURI().toString(), 300, 200, true, true);
                imageView.setImage(img);
            } else {
                // Если файла нет на диске, пробуем загрузить стандартную заглушку из ресурсов
                var resourceStream = getClass().getResourceAsStream("/images/picture.png");
                if (resourceStream != null) {
                    imageView.setImage(new Image(resourceStream, 300, 200, true, true));
                } else {
                    imageView.setImage(null);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при показе картинки: " + e.getMessage());
            imageView.setImage(null);
        }
    }

    private void saveImageIfChanged() throws Exception {

        if (selectedImageFile == null) return;

        Path imagesDir = Paths.get("images");
        if (!Files.exists(imagesDir)) {
            Files.createDirectories(imagesDir);
        }

        String newFileName = System.currentTimeMillis() + "_" + selectedImageFile.getName();
        Path destination = imagesDir.resolve(newFileName);

        Files.copy(selectedImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        // Удаляем старое изображение
        if (oldImagePath != null && !oldImagePath.equals("picture.png")) {
            Files.deleteIfExists(imagesDir.resolve(oldImagePath));
        }

        currentImagePath = newFileName;
    }

    @FXML
    private void handleBack() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Вы уверены, что хотите выйти? Несохраненные данные будут потеряны.", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Подтверждение");
        if (alert.showAndWait().get() == ButtonType.YES) {
            closeWindow();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String generateRandomArticle() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        java.util.Random random = new java.util.Random();

        return "" +
               letters.charAt(random.nextInt(letters.length())) +
               digits.charAt(random.nextInt(digits.length())) +
               digits.charAt(random.nextInt(digits.length())) +
               digits.charAt(random.nextInt(digits.length())) +
               letters.charAt(random.nextInt(letters.length())) +
               digits.charAt(random.nextInt(digits.length()));
    }
}