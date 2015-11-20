package com.akka.testProject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Iaroslav on 11/11/2015.
 */
public class DataService {
    private CalculationServiceFactory calculationServiceFactory;

    public DataService() {
        calculationServiceFactory = new CalculationServiceFactory();
    }

    /**
     * Method open aggregates data from sourceFile and write it to destinationFile.
     *
     * @param sourceFile      name of file, from which to read data.
     * @param destinationFile name of file, to which to write data.
     * @param actorPoolSize   size of {@link akka.routing.Router} of data handlers {@link WorkerActor}.
     * @param processTime     milliseconds of awaiting for actor's work result.
     */
    public void aggregateData(String sourceFile, String destinationFile, int actorPoolSize, long processTime) {
        CalculationService calculationService = calculationServiceFactory.getCalcServiceInstance(actorPoolSize);
        try {
            Files.lines(Paths.get(sourceFile))
                    .map(q -> q.split(";"))
                    .forEach(v -> calculationService.aggregateAmount(new Row(Long.valueOf(v[0]), Long.valueOf(v[1]))));
            Map<Long, Row> result = calculationService.getResults(processTime);
            calculationService.stopActors();
            Files.write(Paths.get(destinationFile), result.values().stream()
                    .map(r -> r.getId() + ";" + r.getAmount())
                    .collect(Collectors.toList()));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
