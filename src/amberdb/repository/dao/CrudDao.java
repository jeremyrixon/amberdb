package amberdb.repository.dao;

import amberdb.repository.model.Node;

import java.util.List;

public interface CrudDao<T extends Node> {
    T get(Long id);
    List<T> getHistory(Long id);

    Long insert(T instance);
    Long insertHistory(T instance);

    T save(T instance);
    T saveHistory(T instance);

    void delete(Long id);
    void deleteHistory(Long id);

    // below methods update both the main and history tables
    void add(T instance);
    void update(T instance);
    void remove(Long id);

    int count();
    boolean exists(Long id);
}
