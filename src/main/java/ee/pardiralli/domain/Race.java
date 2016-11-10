package ee.pardiralli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Race implements Comparable<Race> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Integer id;

    private Date beginning;

    private Date finish;

    private String name;

    private String description;

    @NotNull
    private Boolean isOpen;

    public Race(Date beginning, Date finish) {
        this.beginning = beginning;
        this.finish = finish;
    }

    @Override
    public int compareTo(Race o) {
        return this.beginning.compareTo(o.getBeginning());
    }
}
