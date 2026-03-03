package com.example.shoe_store.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class Order {
    private int orderId;
    private String articleNumber;
    private String userName;
    private String status;
    private String pickupPoint;
    private String pickupCode;
    private LocalDate orderDate;
    private LocalDate pickupDate;

}