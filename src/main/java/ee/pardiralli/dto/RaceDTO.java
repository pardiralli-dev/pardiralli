package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceDTO implements Comparable<RaceDTO>  {

    private Integer id;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @NotNull
    private java.util.Date beginning;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @NotNull
    private java.util.Date finish;

    @NotNull
    private String raceName;

    @NotNull
    private String description;

    @NotNull
    private Boolean isOpen;

    @NotNull
    private Boolean isNew;

    public String getBeginningAsString(){
        return new SimpleDateFormat("dd-MM-yyyy").format(beginning);
    }

    public String getEndAsString(){
        return new SimpleDateFormat("dd-MM-yyyy").format(finish);
    }


    @Override
    public int compareTo(RaceDTO o) {
        return this.beginning.compareTo(o.getBeginning());
    }
}
