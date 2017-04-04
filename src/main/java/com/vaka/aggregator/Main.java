package com.vaka.aggregator;


import com.vaka.aggregator.service.AggregationService;
import com.vaka.aggregator.service.AkkaAggregationServiceImpl;
import com.vaka.aggregator.service.FileSummary;
import com.vaka.aggregator.util.FileSummaryFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.OptionalInt;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Created by Iaroslav on 11/11/2015.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Properties props = loadProperties("default.properties");
        String srcPath = props.getProperty("file.src.path");

        TestFileGeneratorUtil.generateFile(srcPath);

        FileSummaryFactory summaryByAkkaAggregatorsFactory =
                new FileSummaryFactory(getAkkaAggregationServiceSupplier(props));
        FileSummary summary = summaryByAkkaAggregatorsFactory.forFile(srcPath);

        String destPath = props.getProperty("file.dest.path");
        summary.writeDataToFile(destPath);

    }

    private static Properties loadProperties(String fileName) throws IOException {
        String propertiesPath = Main.class.getClassLoader().getResource(fileName).getPath();
        Properties props = new Properties();
        props.load(new FileInputStream(propertiesPath));
        return props;
    }


    public static Supplier<AggregationService> getAkkaAggregationServiceSupplier(Properties properties) {
        Integer actorPoolSize = Integer.valueOf(properties.getProperty("actor.pool.size"));
        Integer uniqueRowsCount = Integer.valueOf(properties.getProperty("unique.rows.count"));
        return () -> AkkaAggregationServiceImpl.of(actorPoolSize, uniqueRowsCount);
    }

}
