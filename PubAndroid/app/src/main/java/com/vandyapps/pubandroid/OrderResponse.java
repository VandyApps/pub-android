package com.vandyapps.pubandroid;

import java.util.List;

/**
 * A POJO representation of the JSON we receive from the server.
 *
 * We've structured this so that Retrofit's default GSON converter will
 * automatically demarshall the JSON response into this object.
 */
public class OrderResponse {

    // An actual order object, which includes the order number and the time it was created.
    public static class Order {

        public int orderNumber;
        public long timeCreated;

        public int getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(int orderNumber) {
            this.orderNumber = orderNumber;
        }

        public long getTimeCreated() {
            return timeCreated;
        }

        public void setTimeCreated(long timeCreated) {
            this.timeCreated = timeCreated;
        }

    }

    // The list of orders returned by the server
    public List<Order> orders;

    // The status of the response (Malformed, Invalid, Ok, etc.)
    public String status;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
