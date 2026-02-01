package unicam.it.idshackhub.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
@Component
public final class EntityUtils {
    public static <T> T getEntity(JpaRepository<T, Long> repository, Long id, String entityName) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(entityName + " not found with ID: " + id));
    }
}