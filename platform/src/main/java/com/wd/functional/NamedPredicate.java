package com.wd.functional;

import java.util.function.Predicate;

public class NamedPredicate<T> implements Predicate<T> {

    private final String name;
    private final Predicate<T> predicate;

    public NamedPredicate(String name, Predicate<T> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    @Override
    public boolean test(T t) {
        return predicate.test(t);
    }

    @Override
    public String toString() {
        return name;
    }
}
