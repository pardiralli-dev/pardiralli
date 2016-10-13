package ee.pardiralli.db;

import ee.pardiralli.domain.Duck;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface DuckRepository extends CrudRepository<Duck, Integer> {

    Duck findById(Integer id);

    /**
     * Search item by parameters, parameters can be empty string.
     */
    List<Duck> findDuck(@Param("ownerFirstName") String ownerFirstName,
                        @Param("ownerLastName") String ownerLastName,
                        @Param("email") String email,
                        @Param("phone") String phone,
                        Pageable pageable);
}
