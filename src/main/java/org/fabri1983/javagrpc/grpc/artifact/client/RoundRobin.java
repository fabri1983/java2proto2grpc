package org.fabri1983.javagrpc.grpc.artifact.client;

import java.util.Iterator;
import java.util.List;

public class RoundRobin<T> {

    private Iterator<Robin<T>> it;
    private List<Robin<T>> list;

    public RoundRobin(List<Robin<T>> list) {
        this.list = list;
        it = list.iterator();
    }

    public T next() {
        // if we get to the end, start again
        if (!it.hasNext()) {
            it = list.iterator();
        }
        Robin<T> robin = it.next();

        return robin.get();
    }

    public static class Robin<T> {
        private T i;

        public Robin(T i) {
            this.i = i;
        }

        public T get() {
            return i;
        }
    }
}
