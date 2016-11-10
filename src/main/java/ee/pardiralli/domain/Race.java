package ee.pardiralli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Race implements Comparable<Race> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Date beginning;

    @NotNull
    private Date finish;

    @Size(max=50)
    @NotNull
    private String raceName;

    @Size(max=2000)
    @NotNull
    private String description;

    @NotNull
    private Boolean isOpen;

    public Race(Date beginning, Date finish) {
        this.beginning = beginning;
        this.finish = finish;
    }


    public Race(Date beginning, Date finish, String raceName, String description, Boolean isOpen) {
        this.beginning = beginning;
        this.finish = finish;
        this.raceName = raceName;
        this.description = description;
        this.isOpen = isOpen;
    }




    @Override
    public int compareTo(Race o) {
        return this.beginning.compareTo(o.getBeginning());
    }
}
