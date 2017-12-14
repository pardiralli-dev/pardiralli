package ee.pardiralli.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Race implements Comparable<Race> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private LocalDate beginning;

    @NotNull
    private LocalDate finish;

    @Size(max = 50)
    @NotBlank
    private String raceName;


    @NotNull
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
