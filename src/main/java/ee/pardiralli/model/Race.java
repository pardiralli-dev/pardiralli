package ee.pardiralli.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Race implements Comparable<Race> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate beginning;

    private LocalDate finish;

    private String raceName;

    private Boolean isOpen;

    public Race(LocalDate beginning, LocalDate finish, String raceName, Boolean isOpen) {
        this.beginning = beginning;
        this.finish = finish;
        this.raceName = raceName;
        this.isOpen = isOpen;
    }

    @Override
    public int compareTo(Race o) {
        return this.beginning.compareTo(o.getBeginning());
    }
}
