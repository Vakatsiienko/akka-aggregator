package com.vaka.aggregator.service;

import com.vaka.aggregator.model.Row;
import com.vaka.aggregator.util.CreatingException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The adapter of aggregator service for file data aggregation
 */
public class FileSummary {

    private final AggregationService aggregationService;

    private final String filePath;

    private Map<Long, BigDecimal> amountByIdAggregationResult;

    private FileSummary(AggregationService aggregationService, String filePath) {
        this.aggregationService = aggregationService;
        this.filePath = filePath;
    }

    public static FileSummary of(AggregationService aggregationService, String filePath) throws IOException {
        Validator.validateCreatingParameters(aggregationService, filePath);
        FileSummary fileSummary = new FileSummary(aggregationService, filePath);
        fileSummary.aggregate();
        return fileSummary;
    }

    public void aggregate() throws IOException {
        throwFileDataToAggregationService(filePath);
        amountByIdAggregationResult = Collections.unmodifiableMap(aggregationService.getResult());
    }

    private void throwFileDataToAggregationService(String filePath) throws IOException {
        Stream<Row> rowStream = Files.lines(Paths.get(filePath))
                .map(Row::from);
        aggregationService.aggregate(rowStream);
    }


    public void writeDataToFile(String fileDestination) throws IOException {
        Files.write(Paths.get(fileDestination), amountByIdAggregationResult.entrySet().stream()
                .map(entry -> String.format("%s;%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));
    }


    public static class Validator {
        private static void validateCreatingParameters(AggregationService aggregationService, String filePath) {
            StringJoiner messageJoiner = new StringJoiner("\n");
            if (!aggregationService.ready()) {
                messageJoiner.add("Aggregation service isn't ready");
            }
            File file = new File(filePath);
            if (!file.exists()) {
                messageJoiner.add("File is not exist");
            }
            if (messageJoiner.length() > 0) {
                throw new CreatingException(String.join("\n", "Invalid creating parameters:", messageJoiner.toString()));
            }
        }
    }
}