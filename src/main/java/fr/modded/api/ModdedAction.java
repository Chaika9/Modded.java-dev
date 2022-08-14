package fr.modded.api;

import fr.modded.api.requests.action.operator.FlatMapModdedAction;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ModdedAction<T> {
    Consumer<Object> DEFAULT_SUCCESS = o -> {
    };
    Consumer<? super Throwable> DEFAULT_FAILURE = t -> System.err.printf("Action execute returned failure: %s%n", t.getMessage());

    T execute();

    void executeAsync(Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure);

    default void executeAsync(Consumer<? super T> onSuccess) {
        executeAsync(onSuccess, null);
    }

    default void executeAsync() {
        executeAsync(null, null);
    }

    default <O> ModdedAction<O> flatMap(Function<? super T, ? extends ModdedAction<O>> flatMap) {
        return flatMap(null, flatMap);
    }

    default <O> ModdedAction<O> flatMap(Predicate<? super T> condition, Function<? super T, ? extends ModdedAction<O>> flatMap) {
        return new FlatMapModdedAction<>(this, condition, flatMap);
    }

    ModdedImpl getModded();
}
