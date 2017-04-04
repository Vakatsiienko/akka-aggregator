package com.vaka.aggregator.actor;

import akka.actor.UntypedActor;
import com.vaka.aggregator.model.Row;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

/**
 * Created by Iaroslav on 11/12/2015.
 */
public class RowAggregatorActor extends UntypedActor implements Serializable {
    private Map<Long, BigDecimal> amountByIdRepository;

    public RowAggregatorActor(int repositorySize) {
        this.amountByIdRepository = new HashMap<>(repositorySize);
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Row) {
            mergeToRepository((Row) o);
        } else if (o.toString().equals("getResult")) {
            returnRepository();
        } else unhandled(o);
    }

    private void mergeToRepository(Row row) {
        amountByIdRepository.merge(row.getId(), row.getAmount(), BigDecimal::add);
    }

    private void returnRepository() {
        getSender().tell(amountByIdRepository, getSelf());
    }

}

