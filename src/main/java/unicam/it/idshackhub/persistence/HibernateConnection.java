package unicam.it.idshackhub.persistence;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Getter
public class HibernateConnection implements Persistence {
    private static volatile HibernateConnection instance;
    private SessionFactory sessionFactory;

    public static HibernateConnection getInstance() {
        if (instance == null) {
            synchronized (HibernateConnection.class) {
                if (instance == null) {
                    instance = new HibernateConnection();
                }
            }
        }
        if (instance.sessionFactory == null) {
            instance.open();
        }
        return instance;
    }

    @Override
    public void open() {
        if (sessionFactory == null) {
            try {
                sessionFactory = new Configuration().configure().buildSessionFactory();
            } catch (Exception ex) {
                throw new RuntimeException("Error while initializing Hibernate", ex);
            }
        }
    }

    @Override
    public void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }

    public Session openSession() {
        if (sessionFactory == null) {
            open();
        }
        return sessionFactory.openSession();
    }
}
