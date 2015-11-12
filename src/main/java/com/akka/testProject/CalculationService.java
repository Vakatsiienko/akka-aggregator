package com.akka.testProject;

import akka.actor.*;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Awaitable;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.Map;

/**
 * Created by Iaroslav on 11/11/2015.
 */
public class CalculationService {

    private final ActorRef masterActor;

    private final ActorSystem system;

    /**
     * @param actorPoolSize number of {@link WorkerActor} that will be instantiated to {@link akka.routing.Router}
     *                      of {@link MasterActor} this CalculationService
     */
    public CalculationService(int actorPoolSize) {
        this.system = ActorSystem.create();
        masterActor = system.actorOf(Props.create(MasterActor.class, actorPoolSize), "masterActor");
        masterActor.tell("initializeWorkers", ActorRef.noSender());
    }

    /**
     * Aggregate sending {@link Row} with other sending Row's to one Map with ID keys.
     */
    public void aggregateAmount(Row row) {
        masterActor.tell(row, ActorRef.noSender());
    }

    /**
     * @param mSecToOut milliseconds to wait for result of future call
     * @return Map of all sending {@link Row}'s by {@link #aggregateAmount(Row)}
     * @throws Exception
     * @see Patterns#ask(ActorRef, Object, Timeout)
     * @see Await#result(Awaitable, Duration)
     */
    public Map<Long, Row> getResults(long mSecToOut) throws Exception {
        Timeout timeout = new Timeout(Duration.create(mSecToOut, "milliseconds"));
        Future<Object> future = Patterns.ask(masterActor, 1, timeout);
        return (Map) Await.result(future, timeout.duration());
    }

    /**
     * <p>Method call {@link ActorSystem#terminate} and terminates this actor system.
     * This will stop the guardian actor, which in turn will recursively stop all its child actors,
     * then the system guardian (below which the logging actors reside) and the execute all
     * registered termination handlers.</p>
     * @see ActorSystem#terminate()
     */
    public void stopActors() {
        system.terminate();
    }

}
