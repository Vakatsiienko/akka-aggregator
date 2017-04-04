package com.vaka.aggregator.model;

import java.math.BigDecimal;

/**
 * Created by Iaroslav on 11/11/2015.
 */
public class Row {

    private final long id;
    private final BigDecimal amount;

    public Row(long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param str - String in format id;amount
     * @return new Row with given parameters
     */
    public static Row from(String str) {
        String[] arr = str.split(";");
        return new Row(Long.valueOf(arr[0]), new BigDecimal(arr[1]));
    }
}
