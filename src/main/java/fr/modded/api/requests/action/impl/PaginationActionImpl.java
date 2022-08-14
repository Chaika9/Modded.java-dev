package fr.modded.api.requests.action.impl;

import fr.modded.api.ModdedImpl;
import fr.modded.api.PaginationAction;
import fr.modded.api.requests.Route;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PaginationActionImpl<T> extends ModdedActionImpl<List<T>> implements PaginationAction<T> {
    protected final List<T> cached = new CopyOnWriteArrayList<>();

    protected final AtomicInteger limit;

    protected volatile int currentIndex = 0;
    protected volatile int iteratorIndex = 0;

    protected volatile boolean useCache = true;

    public PaginationActionImpl(ModdedImpl modded, Route route, JSONObject data) {
        super(modded, route, data);
        this.limit = new AtomicInteger(200);
    }

    @Override
    public PaginationAction<T> limit(int limit) {
        this.limit.set(limit);
        return this;
    }

    @Override
    public int getLimit() {
        return limit.get();
    }

    @Override
    public PaginationAction<T> skipTo(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index must be >= 0");
        }
        this.currentIndex = index;
        return this;
    }

    @Override
    public int getCurrentIndex() {
        return currentIndex;
    }

    @Override
    public PaginationAction<T> cache(boolean enableCache) {
        this.useCache = enableCache;
        return this;
    }

    @Override
    public List<T> getCached() {
        return cached;
    }

    @Override
    public PaginationIterator<T> iterator() {
        return new PaginationIterator<>(cached, this::getNextChunk);
    }

    protected int getIteratorIndex() {
        return iteratorIndex < cached.size() ? cached.size() - iteratorIndex : -1;
    }

    protected List<T> getRemainingCache() {
        int index = getIteratorIndex();
        if (useCache && index > -1 && index < cached.size()) {
            return cached.subList(index, cached.size());
        }
        return Collections.emptyList();
    }

    public List<T> getNextChunk() {
        final List<T> list = getRemainingCache();
        if (!list.isEmpty()) {
            return list;
        }
        return execute();
    }

    @Override
    protected JSONObject finalizeData() {
        JSONObject data = super.finalizeData();
        if (data == null) {
            data = new JSONObject();
        }
        data.put("index", getCurrentIndex());
        data.put("limit", limit.get());
        return data;
    }
}
