package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceDTO implements Comparable<RaceDTO> {
    private Integer id;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @NotNull
    private LocalDate beginning;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @NotNull
    private LocalDate finish;

    @NotNull
    @Size(max = 50)
    private String raceName;

    private Boolean isOpen;

    public String getBeginningAsString() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy").format(beginning);
    }

    public String getEndAsString() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy").format(finish);
    }

    @Override
    public int compareTo(RaceDTO o) {
        return o.getBeginning().compareTo(this.beginning);
    }
}
