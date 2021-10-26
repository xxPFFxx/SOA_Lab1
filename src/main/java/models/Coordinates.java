package models;

import lombok.*;

import javax.persistence.*;
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

    private Integer x; //Максимальное значение поля: 416, Поле не может быть null

    private Long y; //Значение поля должно быть больше -700, Поле не может быть null

    @OneToMany(mappedBy = "coordinates", fetch = FetchType.EAGER)
    private List<HumanBeing> humanBeings;

    public Coordinates(Integer x, Long y){
        this.x = x;
        this.y = y;
    }
}
