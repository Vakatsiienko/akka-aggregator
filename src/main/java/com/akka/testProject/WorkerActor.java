package com.akka.testProject;

import akka.actor.UntypedActor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iaroslav on 11/12/2015.
 */
public class WorkerActor extends UntypedActor implements Serializable {
    private Map<Long, Row> result = new HashMap<>();

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Row) {
            Row row = (Row) o;
            if (result.containsKey(row.getId())) result.put(row.getId(), result.get(row.getId()).sumAmount(row));
            else result.put(row.getId(), row);
        } else if (o instanceof Integer)
            getSender().tell(result, getSelf());
        else unhandled(o);
    }
}

