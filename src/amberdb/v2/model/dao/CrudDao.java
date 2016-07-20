package amberdb.v2.model.dao;

public interface CrudDao<T> {
    T get(Long id);

    Long insert(T instance);

    T save(T instance);

    void delete(Long id);
}
