package com.bookmap.model;

import com.bookmap.internal.RequestRejectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Exchange implements QueryInterface, ExchangeInterface {

    private List<Order> restingOrders = new ArrayList<>();

    public List<Order> getRestingOrders() {
        return restingOrders;
    }

    @Override
    public void send(long orderId, boolean isBuy, int price, int size) throws RequestRejectedException {
        Order order = new Order(orderId, isBuy, price, size);
        List<Order> sortedOrders;
        if (order.isBuy()) {
            sortedOrders = restingOrders.stream().filter(o -> !o.isBuy() && o.getPrice() <= order.getPrice()).sorted((o1, o2) -> o1.compare(o1, o2)).collect(Collectors.toList());
        } else {
            sortedOrders = restingOrders.stream().filter(o -> o.isBuy() && o.getPrice() >= order.getPrice()).sorted((o1, o2) -> o1.compare(o1, o2)).collect(Collectors.toList());
        }
        matchOrders(order, sortedOrders);
    }

    private void matchOrders(Order order, List<Order> sortedOrders) {
        Order nextOrder = null;
        while (order.getSize() > 0 && sortedOrders.iterator().hasNext()) {
            nextOrder = sortedOrders.iterator().next();
            if (nextOrder.getSize() <= order.getSize()) {
                restingOrders.remove(nextOrder);
                sortedOrders.remove(nextOrder);
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
}
