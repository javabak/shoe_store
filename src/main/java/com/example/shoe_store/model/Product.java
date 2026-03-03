package com.example.shoe_store.model;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class Product {
    private int productId;
    private String article;
    private String name;
    private String description;
    private String category;
    private String manufacturer;
    private String supplier;
    private double price;
    private String unit;
    private int quantity;
    private String imagePath;
    private double discount;


    public boolean hasDiscount() {
        return discount > 0;
    }

    public boolean isHighDiscount() {
        return discount > 15;
    }

    public boolean isOutOfStock() {
        return quantity <= 0;
    }

    public double getFinalPrice() {
        if (discount <= 0) {
            return price;
        }
        return price - (price * discount / 100);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Product) obj;
        return this.productId == that.productId &&
               Objects.equals(this.article, that.article) &&
               Objects.equals(this.name, that.name) &&
               Objects.equals(this.description, that.description) &&
               Objects.equals(this.category, that.category) &&
               Objects.equals(this.manufacturer, that.manufacturer) &&
               Objects.equals(this.supplier, that.supplier) &&
               Double.doubleToLongBits(this.price) == Double.doubleToLongBits(that.price) &&
               Objects.equals(this.unit, that.unit) &&
               this.quantity == that.quantity &&
               Objects.equals(this.imagePath, that.imagePath) &&
               Double.doubleToLongBits(this.discount) == Double.doubleToLongBits(that.discount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, article, name, description,
                category, manufacturer, supplier, price, unit, quantity, imagePath, discount);
    }

    @Override
    public String toString() {
        return "Product[" +
               "productId=" + productId + ", " +
               "article=" + article + ", " +
               "name=" + name + ", " +
               "description=" + description + ", " +
               "category=" + category + ", " +
               "manufacturer=" + manufacturer + ", " +
               "supplier=" + supplier + ", " +
               "price=" + price + ", " +
               "unit=" + unit + ", " +
               "quantity=" + quantity + ", " +
               "imagePath=" + imagePath + ", " +
               "discount=" + discount + ']';
    }


}