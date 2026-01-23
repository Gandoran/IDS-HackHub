package unicam.it.idshackhub.controller;

import lombok.Getter;
import org.hibernate.Session;
import unicam.it.idshackhub.persistence.HibernateConnection;

import java.util.Arrays;
import java.util.List;

@Getter
public abstract class AbstractController<T> implements Controller<T> {
    private final HibernateConnection hibernateConnection;
    private final Class<T> entityClass;

    /**
     * Abstract class constructor.
     *
     * @param entityClass the class of the entity to be managed
     */
    protected AbstractController(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.hibernateConnection = HibernateConnection.getInstance();
    }

    /**
     * Adds a new entity to the database.
     *
     *
     * @param transaction the entity to add
     * @throws RuntimeException if an error occurs during saving
     */
    @Override
    public void add(T transaction) {
        try (var session = hibernateConnection.openSession()) {
            session.beginTransaction();
            session.persist(transaction);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes an entity from the database given its ID.
     *
     * @param id the identifier of the entity to remove
     * @throws RuntimeException if the entity is not found or an error occurs during deletion
     */
    @Override
    public void remove(long id) {
        try (var session = hibernateConnection.openSession()) {
            session.beginTransaction();
            T entity = find(session, id);
            session.remove(entity);
            session.getTransaction().commit();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns all entities of the managed type present in the database.
     *
     * @return a list of all present entities
     * @throws RuntimeException if an error occurs during reading
     */
    @Override
    public List<T> get() {
        String hql = "From "+ entityClass.getSimpleName();
        try(var session = hibernateConnection.openSession()) {
            return session.createQuery(hql, entityClass).getResultList();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates an existing entity with non-null fields from the provided entity.
     *
     * @param id the ID of the entity to update
     * @param entity the entity containing the new values
     * @throws RuntimeException if the entity is not found or an error occurs during the update
     */
    @Override
    public void update(long id, T entity) {
        try (var session = hibernateConnection.openSession()) {
            session.beginTransaction();
            T existingEntity = find(session, id);
            copyNonNullFields(entity, existingEntity);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Copies all non-null fields from the source entity to the target entity,
     * excluding the 'id' field.
     *
     * @param source source entity with the new values
     * @param target target entity to be updated
     * @throws RuntimeException if a reflection access error occurs
     */
    private void copyNonNullFields(T source, T target) {
        Class<?> clazz = source.getClass();
        while (clazz != null && clazz != Object.class) {
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(f -> !f.getName().equals("id"))
                    .forEach(field -> {
                        try {
                            field.setAccessible(true);
                            field.set(target, field.get(source));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Failed to copy field: " + field.getName(), e);
                        }
                    });
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Searches for an entity in the database by its ID.
     *
     * @param session the Hibernate session in use
     * @param id the ID of the entity to search for
     * @return the found entity
     * @throws RuntimeException if the entity is not found
     */
    private T find(Session session, long id) {
        T entity = session.find(entityClass, id);
        if (entity == null) {
            throw new RuntimeException("Entity with id " + id + " not found");
        }
        return entity;
    }
}