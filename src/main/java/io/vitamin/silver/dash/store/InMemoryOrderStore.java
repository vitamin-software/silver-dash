package io.vitamin.silver.dash.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class InMemoryOrderStore<K, T> implements Store<K, T> {

    private final ConcurrentMap<K, T> store;
    private final Supplier<K> idGenerator;

    public InMemoryOrderStore(Supplier<K> idGenerator) {
        this.idGenerator = idGenerator;
        this.store = new ConcurrentHashMap<>();
    }

    @Override
    public K add(T item) {
        final K id =idGenerator.get();
        store.put(id, item);
        return id;
    }

    @Override
    public T remove(K id){
        return store.remove(id);
    }

    @Override
    public Collection<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public T find(K id) {
        return store.get(id);
    }
}
