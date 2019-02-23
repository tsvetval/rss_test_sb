package ru.rss.search.elasticsearch;

import java.util.Optional;

public interface IndexRepository<T> {

    Optional<T> findOne(String entityId);

    T save(T entity);

    void remove(String entityId);

}
