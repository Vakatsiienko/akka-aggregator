package com.vaka.aggregator.util;

/**
 * Created by Iaroslav on 3/29/2017.
 */
@FunctionalInterface
public interface SupplierWithException<T> {
    T get() throws Exception;
}
