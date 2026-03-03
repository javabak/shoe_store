package com.example.shoe_store.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

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


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Order) obj;
        return this.orderId == that.orderId &&
               Objects.equals(this.articleNumber, that.articleNumber) &&
               Objects.equals(this.userName, that.userName) &&
               Objects.equals(this.status, that.status) &&
               Objects.equals(this.pickupPoint, that.pickupPoint) &&
               Objects.equals(this.pickupCode, that.pickupCode) &&
               Objects.equals(this.orderDate, that.orderDate) &&
               Objects.equals(this.pickupDate, that.pickupDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, articleNumber, userName, status, pickupPoint, pickupCode, orderDate, pickupDate);
    }

    @Override
    public String toString() {
        return "Order[" +
               "orderId=" + orderId + ", " +
               "articleNumber=" + articleNumber + ", " +
               "userName=" + userName + ", " +
               "status=" + status + ", " +
               "pickupPoint=" + pickupPoint + ", " +
               "pickupCode=" + pickupCode + ", " +
               "orderDate=" + orderDate + ", " +
               "pickupDate=" + pickupDate + ']';
    }


}