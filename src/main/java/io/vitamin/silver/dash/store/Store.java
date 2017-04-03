package io.vitamin.silver.dash.store;

import java.util.Collection;

public interface Store<K, T> {
    T remove(K id);
    K add(T order);
    Collection<T> findAll();
    T find(K id);
}
