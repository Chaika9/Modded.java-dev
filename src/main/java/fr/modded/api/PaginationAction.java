package fr.modded.api;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface PaginationAction<T> extends ModdedAction<List<T>>, Iterable<T> {
    PaginationAction<T> limit(int limit);

    int getLimit();

    PaginationAction<T> skipTo(int index);

    int getCurrentIndex();

    PaginationAction<T> cache(boolean enableCache);

    List<T> getCached();

    @Override
    PaginationIterator<T> iterator();

    @Override
    default Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.IMMUTABLE);
    }

    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    class PaginationIterator<E> implements Iterator<E> {
        private Queue<E> items;
        private final Supplier<List<E>> supply;

        public PaginationIterator(Collection<E> queue, Supplier<List<E>> supply) {
            this.items = new LinkedList<>(queue);
            this.supply = supply;
        }

        @Override
        public boolean hasNext() {
            if (items == null) {
                return false;
            }
            if (!hitEnd()) {
                return true;
            }

            if (items.addAll(supply.get())) {
                return true;
            }

            items = null;
            return false;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Reached end of pagination task");
            }
            return items.poll();
        }

        protected boolean hitEnd() {
            return items.isEmpty();
        }
    }
}
