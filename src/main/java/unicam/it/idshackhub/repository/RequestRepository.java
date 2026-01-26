package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.it.idshackhub.model.utils.Request;

/**
 * Spring Data repository for {@link Request} entities.
 */
@Repository
public interface RequestRepository extends JpaRepository<Request,Long> {
}
