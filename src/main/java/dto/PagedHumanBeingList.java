package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.HumanBeing;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PagedHumanBeingList {
    private List<HumanBeing> humanBeingList;
    private Long count;
}