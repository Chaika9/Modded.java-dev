package fr.modded.api.requests.action.operator;

import fr.modded.api.ModdedAction;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DelayModdedAction<T> extends ModdedActionOperator<T, T> {
    private final TimeUnit unit;
    private final long delay;

    public DelayModdedAction(ModdedAction<T> action, TimeUnit unit, long delay) {
        super(action);
        this.unit = unit;
        this.delay = delay;
    }

    @Override
    public void executeAsync(Consumer<? super T> success, Consumer<? super Throwable> failure) {
        action.executeAsync((result) -> getModded().getScheduler().schedule(() -> doSuccess(success, result), delay, unit), failure);
    }

    @Override
    public T execute() {
        final T result = action.execute();
        try {
            unit.sleep(delay);
            return result;
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }
}
