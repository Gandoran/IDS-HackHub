package unicam.it.idshackhub.controller;

import java.util.List;

public interface Controller<T> {
    void add(T entity);
    void remove(long id);
    void update(long id,T entity);
    List<T> get();
    Class<T> getEntityClass();
}