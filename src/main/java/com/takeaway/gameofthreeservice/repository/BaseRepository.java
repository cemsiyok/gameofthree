package com.takeaway.gameofthreeservice.repository;

public interface BaseRepository<K, T> {

    T findById(K id);

    T save(T entity);
}