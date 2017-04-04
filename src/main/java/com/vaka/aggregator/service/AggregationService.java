package com.vaka.aggregator.service;

import com.vaka.aggregator.model.Row;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Iaroslav on 3/30/2017.
 */
public interface AggregationService {

    void aggregate(Stream<Row> stream);

    Map<Long, BigDecimal> getResult();

    boolean ready();
}
