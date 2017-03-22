package ee.pardiralli.wp;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;

public interface UserMetaRepository extends CrudRepository<PrUsermeta, BigInteger> {

    @Query("SELECT meta.metaValue FROM WpUsermeta meta, WpUsers user " +
            "WHERE user = :user AND meta.user = user AND meta.metaKey = 'wp_capabilities'")
    String findCapsByUser(@Param("user") PrUsers user);

}
