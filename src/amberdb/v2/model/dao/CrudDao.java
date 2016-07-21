package amberdb.v2.model.dao;

import java.util.List;

public interface CrudDao<T> {
    T get(Long id);

    Long insert(T instance);

    T save(T instance);

    void delete(Long id);

    List<T> getHistory(Long id);

    Long insertHistory(T instance);

    T saveHistory(T instance);

    void deleteHistory(Long id);
}
