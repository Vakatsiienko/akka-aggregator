package com.akka.testProject;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Iaroslav on 11/12/2015.
 */
public class MasterActor extends UntypedActor implements Serializable {
    public MasterActor(int actorPoolSize) {
        this.actorPoolSize = actorPoolSize;
    }
    /**
     * Initialization Router.
     */
    private Router router;
    private Map<Long, Row> result = new HashMap<>();
    private int numOfRecievedMaps;
    private ActorRef futureRef;
    private int actorPoolSize;

    public void initializeWorkers() {
        List<Routee> routees = new ArrayList<>(actorPoolSize);
        for (int i = 0; i < actorPoolSize; i++) {
            ActorRef r = getContext().actorOf(Props.create(WorkerActor.class), i + "");
            getContext().watch(r);
            routees.add(new ActorRefRoutee(r));
        }
        router = new Router(new RoundRobinRoutingLogic(), routees);
    }



    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Row) {
            router.route(o, getSelf());
        } else if (o instanceof String && o.equals("initializeWorkers")) {
            this.initializeWorkers();
        } else if (o instanceof Integer) {
            futureRef = getSender();
            router.route(new Broadcast(0), getSelf());
        } else if (o instanceof Map) {
            numOfRecievedMaps++;
            Map<Long, Row> recieved = (Map) o;
            for (Map.Entry<Long, Row> entry : recieved.entrySet()) {
                if (result.containsKey(entry.getKey()))
                    result.put(entry.getKey(), result.get(entry.getKey()).sumAmount(entry.getValue()));
                else result.put(entry.getKey(), entry.getValue());
            }
            if (numOfRecievedMaps == actorPoolSize) {
                futureRef.tell(result, getSelf());
            }
        } else unhandled(o);
    }


}
