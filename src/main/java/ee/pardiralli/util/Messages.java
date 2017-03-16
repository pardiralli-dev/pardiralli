package ee.pardiralli.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
public class Messages {

    /**
     * Maps phone numbers of owners to the serial numbers of ducks bought for them.
     */
    @NotEmpty
    private Map<String, List<String>> serialNrMap;
}
