package com.vaka.aggregator.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import akka.routing.*;
import com.vaka.aggregator.model.Row;
import com.vaka.aggregator.util.CreatingException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Iaroslav on 11/12/2015.
 */
public class MasterActor extends UntypedActor implements Serializable {

    private MasterActor(int actorPoolSize, int defaultUniqueRowsCount) {
        this.actorPoolSize = actorPoolSize;
        this.amountByIdRepository = new HashMap<>(defaultUniqueRowsCount);
    }

    private Router router;
    private Map<Long, BigDecimal> amountByIdRepository;
    private int acceptedRepositories;
    private int actorPoolSize;

    /**
     * Our constructor
     * @return instance of MasterActor with initialized workers
     */
    public static MasterActor of(int actorPoolSize, int defaultUniqueRowsCount) {
        Validator.validateCreatingParameters(actorPoolSize, defaultUniqueRowsCount);
        MasterActor actor = new MasterActor(actorPoolSize, defaultUniqueRowsCount);
        actor.initializeWorkers(defaultUniqueRowsCount);
        return actor;
    }


    private void initializeWorkers(int defaultUniqueRowsCount) {
        List<Routee> routes = new ArrayList<>(actorPoolSize);
        for (int i = 0; i < actorPoolSize; i++) {
            ActorRef r = getContext().actorOf(Props.create(RowAggregatorActor.class,
                    (Creator<RowAggregatorActor>) () -> new RowAggregatorActor(defaultUniqueRowsCount)
            ));
            getContext().watch(r);
            routes.add(new ActorRefRoutee(r));
        }
        router = new Router(new RoundRobinRoutingLogic(), routes);
    }


    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Row) {
            delegateToRowAggregator(o);
        } else if (o instanceof Map) {
            mergeToRepository((Map<Long, BigDecimal>) o);
            acceptedRepositories++;
        } else if (o.toString().equals("endOfData")) {
            askAggregatorsForResult();
        } else if (o.toString().equals("getResult")) {
            if (areAllAggregatorsAccepted()) {
                getSender().tell(Optional.of(amountByIdRepository), ActorRef.noSender());
            } else getSender().tell(Optional.empty(),ActorRef.noSender());
        } else unhandled(o);
    }

    private void delegateToRowAggregator(Object o) {
        router.route(o, getSelf());
    }


    private void askAggregatorsForResult() {
        router.route(new Broadcast("getResult"), getSelf());
    }

    private void mergeToRepository(Map<Long, BigDecimal> map) {
        map.keySet().parallelStream()
                .forEach(key -> amountByIdRepository.merge(key, map.get(key), BigDecimal::add));
    }

    private boolean areAllAggregatorsAccepted() {
        return acceptedRepositories == actorPoolSize;
    }

    public static class Validator {
        private static void validateCreatingParameters(int actorPoolSize, int uniqueRowsCount) {
            StringJoiner messageJoiner = new StringJoiner("\n");
            if (actorPoolSize < 1) {
                messageJoiner.add(String.format("actorPoolSize can't be lower than 1, given - %s", actorPoolSize));
            }
            if (uniqueRowsCount < 1) {
                messageJoiner.add(String.format("defaultUniqueRowsCount can't be lower than 1, given - %s", uniqueRowsCount));
            }
            if (messageJoiner.length() < 0) {
                throw new CreatingException(String.join("\n", "Invalid creating parameters:", messageJoiner.toString()));
            }
        }
    }
}
