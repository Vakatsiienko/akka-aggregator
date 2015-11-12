package com.akka.testProject;

/**
 * Created by Iaroslav on 11/11/2015.
 */
public class Row {

    private long id;
    private long amount;

    public Row(long id, long amount) {
        this.id = id;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public long getAmount() {
        return amount;
    }

    public Row sumAmount(Row row) {
        this.amount += row.amount;
        return this;
    }
}
