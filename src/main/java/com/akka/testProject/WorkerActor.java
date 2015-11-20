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

    /**
     * <p>If handler receive {@link Row} instance - method will aggregate row to result map.</p>
     * <p>if String "getResult" - send result map to sender.</p>
     * @throws Exception
     */
    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Row) {
            Row row = (Row) o;
            result.merge(row.getId(), row, Row::sumAmount);
        } else if (o instanceof String && o.equals("getResult"))
            getSender().tell(result, getSelf());
        else unhandled(o);
    }
}

