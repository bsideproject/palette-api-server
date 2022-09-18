package com.palette.common;

import com.querydsl.core.types.Predicate;
import java.util.Optional;
import java.util.function.Function;

public class BaseRepository {

    protected <T> Predicate condition(T value, Function<T, Predicate> function) {
        return Optional.ofNullable(value)
            .map(function)
            .orElse(null);
    }

}
