package com.bookmap.model;

import java.util.Objects;

public class Order implements Comparable<Order> {

    private long id;
    private boolean isBuy;
    private int price;
    private int size;

    public Order(long id, boolean isBuy, int price, int size) {
        this.id = id;
        this.isBuy = isBuy;
        this.price = price;
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public int getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return id == order.id &&
                isBuy == order.isBuy &&
                price == order.price &&
                size == order.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isBuy, price, size);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", isBuy=" + isBuy +
                ", price=" + price +
                ", size=" + size +
                '}';
    }

    @Override
    public int compareTo(Order o) {
        if (this.getPrice() > o.getPrice()) {
            return !isBuy ? 1 : -1;
        } else if (this.getPrice() < o.getPrice()) {
            return !isBuy ? -1 : 1;
        }
        return 0;
    }
}
