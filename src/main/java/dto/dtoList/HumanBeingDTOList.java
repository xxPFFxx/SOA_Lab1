package dto.dtoList;

import dto.HumanBeingDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.HumanBeing;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class HumanBeingDTOList {
    private List<HumanBeingDTO> humanBeingList;
}
