package ee.pardiralli.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Settings {

    @Getter
    @Setter
    private Boolean isRaceOpen;

    private Race race;

}