package models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "coordinates")
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "x must be not Null value")
    @Max(value = 416, message = "x must be <= 416")
    //@DecimalMax(value = "416.0", message = "x must be <= 416")
    private Integer x; //Максимальное значение поля: 416, Поле не может быть null

    @NotNull(message = "y must be not Null value")
    @Min(value = -700L, message = "y must be > -700")
    //@DecimalMin(value = "-700.0", inclusive = false, message = "y must be > -700")
    private Long y; //Значение поля должно быть больше -700, Поле не может быть null

    @OneToMany(mappedBy = "coordinates", fetch = FetchType.EAGER)
    private List<HumanBeing> humanBeings;

    public Coordinates(Integer x, Long y){
        this.x = x;
        this.y = y;
    }
}
