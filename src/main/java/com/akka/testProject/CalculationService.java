package com.akka.testProject;

import akka.actor.*;
import akka.pattern.Patterns;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.*;
import java.util.Map;

/**
 * Created by Iaroslav on 11/11/2015.
 */
public class CalculationService {


    private final int actorPoolSize;

    private final ActorRef masterActor;

    private final ActorSystem system;


    public CalculationService(int actorPoolSize) {
        this.actorPoolSize = actorPoolSize;
        this.system = ActorSystem.create("helloakka");
        masterActor = system.actorOf(Props.create(MasterActor.class, actorPoolSize), "masterActor");
        masterActor.tell("initializeWorkers", ActorRef.noSender());
    }

    public void aggregateAmount(Row row) {
        masterActor.tell(row, ActorRef.noSender());
    }


    public Map<Long, Row> getResults(long mSecToOut) throws Exception {
        Timeout timeout = new Timeout(Duration.create(mSecToOut, "milliseconds"));
        Future<Object> future = Patterns.ask(masterActor, 1, timeout);
        return (Map) Await.result(future, timeout.duration());
    }

    public void stopActors() {
        system.terminate();
    }

}
