package com.akka.testProject;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.*;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Iaroslav on 11/12/2015.
 */
public class MasterActor extends UntypedActor implements Serializable {
    /**
     *
     * @param actorPoolSize number of workerActor that will be initialize when "initializeWorkers" message was called.
     */
    public MasterActor(int actorPoolSize) {
        this.actorPoolSize = actorPoolSize;
    }

    private Map<Long, Row> result = new HashMap<>();
    private Router router;
    private int numOfReceivedMaps;
    private ActorRef futureRef;
    private int actorPoolSize;

    /**
     * Initialization Router, number of workerActors given in {@link MasterActor} constructor.
     */
    public void initializeWorkers() {
        List<Routee> routees = new ArrayList<>(actorPoolSize);
        for (int i = 0; i < actorPoolSize; i++) {
            ActorRef r = getContext().actorOf(Props.create(WorkerActor.class), i + "");
            getContext().watch(r);
            routees.add(new ActorRefRoutee(r));
        }
        router = new Router(new RoundRobinRoutingLogic(), routees);
    }

    /**
     * <p>If message is instance of {@link Row} - row delegate to worker,</p>
     * <p>if String "initializeWorkers" - {@link #initializeWorkers()} method will call,
     * <p>if String "getResult" - Master will send notifications to all {@link WorkerActor}'s to send
     * their collected data to {@link MasterActor},</p>
     * <p>if Map - collecting all maps to result map, and give this map to sender, who called "collectData".</p>
     * @throws Exception
     */
    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Row) {
            router.route(o, getSelf());
        } else if (o.equals("initializeWorkers")) {
            this.initializeWorkers();
        } else if (o.equals("getResult")) {
            futureRef = getSender();
            router.route(new Broadcast(o), getSelf());
        } else if (o instanceof Map) {
            numOfReceivedMaps++;
            ((Map<Long, Row>) o).forEach((k, v) -> result.merge(k, v, (Row::sumAmount)));
            if (numOfReceivedMaps == actorPoolSize) {
                futureRef.tell(result, getSelf());
            }
        } else unhandled(o);
    }
}
