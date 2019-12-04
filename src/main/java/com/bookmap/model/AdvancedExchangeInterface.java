package com.bookmap.model;

import com.bookmap.internal.RequestRejectedException;

public interface AdvancedExchangeInterface extends ExchangeInterface {
    public void modify(long oid, int newPrice, int newSize,
                       boolean keepPositionOnSizeDecrease) throws RequestRejectedException;
}
