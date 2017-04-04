package com.vaka.aggregator.util;

import com.vaka.aggregator.service.AggregationService;
import com.vaka.aggregator.service.FileSummary;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Created by Iaroslav on 3/30/2017.
 */
public class FileSummaryFactory {
    private Supplier<AggregationService> supplier;

    public FileSummaryFactory(Supplier<AggregationService> supplier) {
        this.supplier = supplier;
    }

    public FileSummary forFile(String filePath) throws IOException {
        AggregationService aggregationService = supplier.get();
        return FileSummary.of(aggregationService, filePath);
    }
}
