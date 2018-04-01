package ee.pardiralli.db;

import ee.pardiralli.model.SysParam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemParamRepository extends JpaRepository<SysParam, String> {
}
