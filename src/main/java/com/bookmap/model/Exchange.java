package com.bookmap.model;

import com.bookmap.internal.RequestRejectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Exchange implements QueryInterface, AdvancedExchangeInterface {

    private List<Order> restingOrders = new ArrayList<>();

    @SuppressWarnings(value = "all")
    public List<Order> getRestingOrders() {
        return restingOrders;
    }

    @SuppressWarnings(value = "all")
    @Override
    public void send(long orderId, boolean isBuy, int price, int size) throws RequestRejectedException {
        Order order = new Order(orderId, isBuy, price, size);
        List<Order> ordersToMatchWith;
        if (order.isBuy()) {
            ordersToMatchWith = restingOrders.stream().filter(o -> o.getPrice() <= order.getPrice() && !o.isBuy()).collect(Collectors.toList());

        } else {
            ordersToMatchWith = restingOrders.stream().filter(o -> o.getPrice() >= order.getPrice() && o.isBuy()).collect(Collectors.toList());
        }
        Collections.sort(ordersToMatchWith);
        matchOrders(order, ordersToMatchWith, restingOrders);
    }

    private void matchOrders(Order order, List<Order> appropriateOrders, List<Order> restingOrders) {
        Order nextOrder = null;
        while (order.getSize() > 0 && appropriateOrders.iterator().hasNext()) {
            nextOrder = appropriateOrders.iterator().next();
            if (nextOrder.getSize() <= order.getSize()) {
                restingOrders.remove(nextOrder);
                appropriateOrders.remove(nextOrder);
            }
            order.setSize(order.getSize() - nextOrder.getSize());
        }
        if (nextOrder != null) {
            nextOrder.setSize(-order.getSize());
        }
        if (order.getSize() > 0) {
            restingOrders.add(order);
        }
    }

    @Override
    public void cancel(long orderId) throws RequestRejectedException {
        if (!restingOrders.removeIf(order -> order.getId() == orderId)) {
            throw new RequestRejectedException();
        }
    }

    @Override
    public int getTotalSizeAtPrice(int price) {
        int size = 0;
        for (Order order : restingOrders) {
            if (order.getPrice() == price)
                size = size + order.getSize();
        }
        return size;
    }

    @Override
    public int getHighestBuyPrice() {
        return restingOrders.stream().filter(Order::isBuy).mapToInt(Order::getPrice).max().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public int getLowestSellPrice() {
        return restingOrders.stream().filter(o -> !o.isBuy()).mapToInt(Order::getPrice).min().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void modify(long oid, int newPrice, int newSize, boolean keepPositionOnSizeDecrease) throws RequestRejectedException {
        Order order = restingOrders.stream().filter(o -> o.getId() == oid).findAny().orElseThrow(RequestRejectedException::new);
        if (keepPositionOnSizeDecrease && newPrice == order.getPrice() && newSize < order.getSize()) {
            order.setSize(newSize);
        } else {
            Order modifiedOrder = new Order(oid, order.isBuy(), newPrice, newSize);

            restingOrders.remove(order);
            restingOrders.add(modifiedOrder);

        }
    }
}
