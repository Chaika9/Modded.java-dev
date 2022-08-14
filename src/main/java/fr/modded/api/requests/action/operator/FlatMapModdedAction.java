package fr.modded.api.requests.action.operator;

import fr.modded.api.ModdedAction;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class FlatMapModdedAction<T, O> extends ModdedActionOperator<T, O> {
    private final Function<? super T, ? extends ModdedAction<O>> function;
    private final Predicate<? super T> condition;

    public FlatMapModdedAction(ModdedAction<T> action, Predicate<? super T> condition, Function<? super T, ? extends ModdedAction<O>> function) {
        super(action);
        this.function = function;
        this.condition = condition;
    }

    @Override
    public O execute() {
        return function.apply(action.execute()).execute();
    }

    @Override
    public void executeAsync(Consumer<? super O> onSuccess, Consumer<? super Throwable> onFailure) {
        action.executeAsync((result) -> {
            if (condition != null && !condition.test(result)) {
                return;
            }

            final ModdedAction<O> then = function.apply(result);
            if (then == null) {
                doFailure(onFailure, new IllegalStateException("FlatMap operand is null"));
            } else {
                then.executeAsync(onSuccess, onFailure);
            }
        }, onFailure);
    }
}
