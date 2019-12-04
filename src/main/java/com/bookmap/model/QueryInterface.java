package com.bookmap.model;

public interface QueryInterface {

    // Return sum of sizes of resting orders at <price> or zero
    public int getTotalSizeAtPrice(int price);

    // Return the highest price with at least one resting Buy order
    public int getHighestBuyPrice();

    // Return the lowest price with at least one resting Sell order
    public int getLowestSellPrice();

}
