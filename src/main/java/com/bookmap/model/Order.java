package com.bookmap.model;

import java.util.Comparator;

public class Order implements Comparator<Order> {

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
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", isBuy=" + isBuy +
                ", price=" + price +
                ", size=" + size +
                '}';
    }

    @Override
    public int compare(Order o1, Order o2) {
        if (o1.getPrice() == o2.getPrice()) {
            return o1.getId() > o2.getId() ? 1 : -1;
        } else {
            return o1.isBuy() ? o2.getPrice() - o1.getPrice() : o1.getPrice() - o2.getPrice();
        }
    }
}
