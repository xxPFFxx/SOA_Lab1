package dto.dtoList;

import dto.CoordinatesDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CoordinatesDTOList {
    private List<CoordinatesDTO> coordinatesList;
}