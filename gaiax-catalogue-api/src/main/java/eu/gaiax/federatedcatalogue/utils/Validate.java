package eu.gaiax.federatedcatalogue.utils;


import eu.gaiax.federatedcatalogue.exception.BadDataException;

import java.util.Objects;
import java.util.function.Function;

public class Validate<T> {
    private T value;
    private boolean match = false;

    private Validate() {
    }

    private Validate(T value) {
        this.value = value;
    }

    public static <V> Validate<V> value(V value) {
        return new Validate<>(value);
    }

    public static <V> Validate<V> isTrue(boolean condition) {
        Validate<V> validate = new Validate<>();
        if (condition) {
            validate.match = true;
        }
        return validate;
    }

    public static <V> Validate<V> isFalse(boolean condition) {
        Validate<V> validate = new Validate<>();
        if (!condition) {
            validate.match = true;
        }
        return validate;
    }

    public static <T> Validate<T> isNull(T value) {
        return new Validate<>(value).isNull();
    }

    public static <T> Validate<T> isNotNull(T value) {
        return new Validate<>(value).isNotNull();
    }

    public Validate<T> inLength(int min, int max) {
        if (Objects.isNull(this.value)) {
            return this;
        }
        if (this.match || this.value.toString().length() < min && this.value.toString().length() > max) {
            this.match = true;
        }
        return this;
    }

    public Validate<T> isNotEmpty() {
        if (this.match || Objects.isNull(this.value) || String.valueOf(this.value).trim().isEmpty()) {
            this.match = true;
        }
        return this;
    }

    public Validate<T> isNull() {
        if (this.match || Objects.isNull(this.value)) {
            this.match = true;
        }
        return this;
    }

    public Validate<T> isNotNull() {
        if (this.match || !Objects.isNull(this.value)) {
            this.match = true;
        }
        return this;
    }

    public Validate<T> check(Function<T, Boolean> checkFunction) {
        if (this.match || checkFunction.apply(this.value)) {
            this.match = true;
        }
        return this;
    }

    public Validate<T> checkNot(Function<T, Boolean> checkFunction) {
        if (this.match || !checkFunction.apply(this.value)) {
            this.match = true;
        }
        return this;
    }

    public T launch(RuntimeException e) {
        if (this.match) {
            throw e;
        }
        return this.value;
    }

    public T launch(String message) {
        if (this.match) {
            throw new BadDataException(message);
        }
        return this.value;
    }

    public boolean calculate() {
        return this.match;
    }
}
