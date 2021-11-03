package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class HumanBeingDTO {
    private String id;
    private String name;
    private CoordinatesDTO coordinates;
    private String creationDate;
    private String realHero;
    private String hasToothpick;
    private String impactSpeed;
    private String soundtrackName;
    private String weaponType;
    private String mood;
    private CarDTO car;
}
