package com.bookmap.model;

import com.bookmap.internal.RequestRejectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Exchange implements QueryInterface, AdvancedExchangeInterface {

    private List<Order> restingBuyOrders = new ArrayList<>();
    private List<Order> restingSellOrders = new ArrayList<>();

    public List<Order> getRestingBuyOrders() {
        return restingBuyOrders;
    }

    public List<Order> getRestingSellOrders() {
        return restingSellOrders;
    }

    private List<Order> getSort(List<Order> orders) {
        Collections.sort(orders);
        return orders;
    }

    @Override
    public void send(long orderId, boolean isBuy, int price, int size) throws RequestRejectedException {
        Order order = new Order(orderId, isBuy, price, size);
        if (order.isBuy()) {
            List<Order> appropriateOrders = getSort(restingSellOrders).stream().filter(o -> o.getPrice() <= order.getPrice()).collect(Collectors.toList());
            matchOrders(order, appropriateOrders, restingBuyOrders, restingSellOrders);
        } else {
            List<Order> appropriateOrders = getSort(restingBuyOrders).stream().filter(o -> o.getPrice() >= order.getPrice()).collect(Collectors.toList());
            matchOrders(order, appropriateOrders, restingSellOrders, restingBuyOrders);
        }
    }

    private void matchOrders(Order order, List<Order> appropriateOrders, List<Order> restingOrdersIn, List<Order> restingOrdersOut) {
        Order nextOrder = null;
        while (order.getSize() > 0 && appropriateOrders.iterator().hasNext()) {
            nextOrder = appropriateOrders.iterator().next();
            if (nextOrder.getSize() <= order.getSize()) {
                restingOrdersOut.remove(nextOrder);
                appropriateOrders.remove(nextOrder);
            }
            order.setSize(order.getSize() - nextOrder.getSize());
        }
        if (nextOrder != null) {
            nextOrder.setSize(-order.getSize());
        }
        if (order.getSize() > 0) {
            restingOrdersIn.add(order);
        }
    }

    @Override
    public void cancel(long orderId) throws RequestRejectedException {
        if (!restingBuyOrders.removeIf(order -> order.getId() == orderId) && !restingSellOrders.removeIf(order -> order.getId() == orderId)) {
            throw new RequestRejectedException();
        }
    }

    @Override
    public int getTotalSizeAtPrice(int price) {
        int size = 0;
        for (Order order : mergedLists()) {
            if (order.getPrice() == price)
                size = size + order.getSize();
        }
        return size;
    }

    private List<Order> mergedLists() {
        List<Order> mergedSet = new ArrayList<>();
        mergedSet.addAll(restingBuyOrders);
        mergedSet.addAll(restingSellOrders);
        return mergedSet;
    }

    @Override
    public int getHighestBuyPrice() {
        return restingBuyOrders.stream().mapToInt(Order::getPrice).max().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public int getLowestSellPrice() {
        return restingSellOrders.stream().mapToInt(Order::getPrice).min().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void modify(long oid, int newPrice, int newSize, boolean keepPositionOnSizeDecrease) throws RequestRejectedException {
        Order order = mergedLists().stream().filter(o -> o.getId() == oid).findAny().orElseThrow(RequestRejectedException::new);
        if (keepPositionOnSizeDecrease && newPrice == order.getPrice() && newSize < order.getSize()) {
            order.setSize(newSize);
        } else {
            Order modifiedOrder = new Order(oid, order.isBuy(), newPrice, newSize);
            if (order.isBuy()) {
                restingBuyOrders.remove(order);
                restingBuyOrders.add(modifiedOrder);
            } else {
                restingSellOrders.remove(order);
                restingSellOrders.add(modifiedOrder);
            }
        }
    }
}
