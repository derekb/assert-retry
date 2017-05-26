package me.alb_i986.testing.assertions.retry;

// TODO replace with Java8 Supplier and create a separate branch for Java7
public interface Supplier<T> {

    T get() throws Exception;
}
