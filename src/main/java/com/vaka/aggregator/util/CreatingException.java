package com.vaka.aggregator.util;

/**
 * Created by Iaroslav on 3/30/2017.
 */
public class CreatingException extends RuntimeException {
    public CreatingException() {
    }

    public CreatingException(String message) {
        super(message);
    }

    public CreatingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreatingException(Throwable cause) {
        super(cause);
    }

    public CreatingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
