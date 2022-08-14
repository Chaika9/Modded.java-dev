package fr.modded.api.requests.action.operator;

import fr.modded.api.ModdedAction;
import fr.modded.api.ModdedImpl;

import java.util.function.Consumer;

public abstract class ModdedActionOperator<T, O> implements ModdedAction<O> {
    protected final ModdedAction<T> action;

    public ModdedActionOperator(ModdedAction<T> action) {
        this.action = action;
    }

    protected static <E> void doSuccess(Consumer<? super E> callback, E value) {
        if (callback == null) {
            DEFAULT_SUCCESS.accept(value);
        } else {
            callback.accept(value);
        }
    }

    protected static void doFailure(Consumer<? super Throwable> callback, Throwable throwable) {
        if (callback == null) {
            DEFAULT_FAILURE.accept(throwable);
        } else {
            callback.accept(throwable);
        }
    }

    @Override
    public ModdedImpl getModded() {
        return action.getModded();
    }
}
