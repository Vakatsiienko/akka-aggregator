package com.akka.testProject;

import java.math.BigDecimal;

/**
 * <p>Row is a model of our data.</p>
 * <br>
 * Created by Iaroslav on 11/11/2015.
 */
public class Row {

    private long id;
    private BigDecimal amount;

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
     * Takes given row and agregate amount to this row.
     * @param row
     * @return Row with this id and sum of rows amount.
     */
    public Row sumAmount(Row row) {
        this.amount = amount.add(row.getAmount());
        return this;
    }
}
