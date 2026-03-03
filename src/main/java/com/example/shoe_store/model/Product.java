package com.example.shoe_store.model;

import lombok.*;

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
}