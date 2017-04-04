package com.vaka.aggregator.service;

import akka.actor.*;
import akka.japi.Creator;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.vaka.aggregator.actor.MasterActor;
import com.vaka.aggregator.model.Row;
import com.vaka.aggregator.util.AggregatorException;
import com.vaka.aggregator.util.SupplierWithException;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Created by Iaroslav on 11/11/2015.
 */
public class AkkaAggregationServiceImpl implements AggregationService {

    private static final Timeout TEN_SECONDS_TIMEOUT = new Timeout(Duration.create(10, TimeUnit.SECONDS));

    private ActorRef masterActor;

    private ActorSystem system;

    private AkkaAggregationServiceImpl() {
    }


    public static AkkaAggregationServiceImpl of(int actorPoolSize, int uniqueRowsCount) {
        AkkaAggregationServiceImpl service = new AkkaAggregationServiceImpl();
        service.system = ActorSystem.create();
        service.masterActor = service.system.actorOf(Props.create(MasterActor.class,
                (Creator<MasterActor>) () -> MasterActor.of(actorPoolSize, uniqueRowsCount)));
        return service;
    }


    /**
     * Aggregate sending {@link Row} with other sending Row's to one Map with ID keys.
     */
    @Override
    public void aggregate(Stream<Row> stream) {
            stream.parallel().forEach(row -> masterActor.tell(row, ActorRef.noSender()));
            masterActor.tell("endOfData", ActorRef.noSender());
    }

    @Override
    public boolean ready() {
        return !system.isTerminated();
    }


    @Override
    public Map<Long, BigDecimal> getResult() {
        try {
            return waitForResult(this::askForResult);
        } catch (Exception e) {
            throw new AggregatorException(e);
        } finally {
            system.shutdown();
        }
    }

    private Optional<Map<Long, BigDecimal>> askForResult() throws Exception {
        Future future = Patterns.ask(masterActor, "getResult", TEN_SECONDS_TIMEOUT);
        return (Optional<Map<Long, BigDecimal>>) Await.result(future, TEN_SECONDS_TIMEOUT.duration());
    }


    private <T> T waitForResult(SupplierWithException<Optional<T>> resultSupplier, long millisBetweenGetFromSupplier) throws Exception {
        while (true) {
            Optional<T> result = resultSupplier.get();
            if (result.isPresent()) {
                return result.get();
            } else {
                Thread.sleep(millisBetweenGetFromSupplier);
            }
        }
    }

    private <T> T waitForResult(SupplierWithException<Optional<T>> function) throws Exception {
        return waitForResult(function, 250);
    }
}
