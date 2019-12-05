package com.bookmap.model;

import com.bookmap.internal.RequestRejectedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExchangeTest {

    @Test
    void testExchange() throws RequestRejectedException {
        Exchange exchange = new Exchange();

        exchange.send(1L, false, 100, 200);
        exchange.send(2L, false, 90, 150);
        exchange.send(3L, false, 200, 300);
        Assertions.assertEquals(650, exchange.getRestingOrders().stream().mapToInt(Order::getSize).sum());
        Assertions.assertEquals(90, exchange.getLowestSellPrice());

        exchange.send(11L, true, 200, 300);
        Assertions.assertEquals(0, exchange.getTotalSizeAtPrice(90));
        Assertions.assertEquals(50, exchange.getTotalSizeAtPrice(100));
        Assertions.assertEquals(300, exchange.getTotalSizeAtPrice(200));

        exchange.send(12L, true, 100, 360);
        Assertions.assertEquals(300, exchange.getTotalSizeAtPrice(200));
        Assertions.assertEquals(310, exchange.getTotalSizeAtPrice(100));

        exchange.send(5L, false, 200, 300);
        exchange.send(6L, false, 100, 50);
        Assertions.assertEquals(600, exchange.getTotalSizeAtPrice(200));
        Assertions.assertEquals(260, exchange.getTotalSizeAtPrice(100));

        exchange.modify(3L, 200, 320, true);
        exchange.send(13L, true, 200, 210);
        Assertions.assertEquals(600 + 20 - 210, exchange.getTotalSizeAtPrice(200));
        Assertions.assertEquals(260, exchange.getTotalSizeAtPrice(100));
        Assertions.assertTrue(exchange.getRestingOrders().contains(new Order(5L, false, 200, 90)));
        Assertions.assertTrue(exchange.getRestingOrders().contains(new Order(3L, false, 200, 320)));

        exchange.send(14L, true, 80, 40);
        Assertions.assertEquals(100, exchange.getHighestBuyPrice());

        exchange.cancel(5L);
        Assertions.assertFalse(exchange.getRestingOrders().contains(new Order(5L, false, 200, 90)));
    }
}