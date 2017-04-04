package com.vaka.aggregator.util;

/**
 * Created by Iaroslav on 3/30/2017.
 */
public class AggregatorException extends RuntimeException {
    public AggregatorException() {
    }

    public AggregatorException(String message) {
        super(message);
    }

    public AggregatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AggregatorException(Throwable cause) {
        super(cause);
    }

    public AggregatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
