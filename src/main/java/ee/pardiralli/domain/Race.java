package ee.pardiralli.domain;

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

    @Size(max=50)
    @NotBlank
    private String raceName;

    @Size(max=2000)
    @NotBlank
    private String description;

    @NotNull
    private Boolean isOpen;

    public Race(LocalDate beginning, LocalDate finish) {
        this.beginning = beginning;
        this.finish = finish;
    }


    public Race(LocalDate beginning, LocalDate finish, String raceName, String description, Boolean isOpen) {
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
