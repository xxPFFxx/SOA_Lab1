package mapper;

import dto.HumanBeingDTO;
import models.Car;
import models.Coordinates;
import models.HumanBeing;
import repository.implementation.CrudRepositoryImplementation;
import util.FieldValidationUtil;

import java.util.ArrayList;
import java.util.List;

public class HumanBeingMapper {
    private CoordinatesMapper coordinatesMapper;
    private CarMapper carMapper;

    public HumanBeingMapper() {
        coordinatesMapper = new CoordinatesMapper();
        carMapper = new CarMapper();
    }

    public HumanBeing mapHumanBeingDTOToHumanBeing(HumanBeingDTO humanBeingDTO) {
        HumanBeing humanBeing = new HumanBeing();
        humanBeing.setId(FieldValidationUtil.getLongFieldValue(humanBeingDTO.getId()));
        humanBeing.setName(FieldValidationUtil.getStringValue(humanBeingDTO.getName()));
        humanBeing.setCoordinates(coordinatesMapper.mapCoordinatesDTOToCoordinates(humanBeingDTO.getCoordinates()));
        humanBeing.setRealHero(FieldValidationUtil.getBooleanFieldValue(humanBeingDTO.getRealHero()));
        humanBeing.setHasToothpick(FieldValidationUtil.getBooleanFieldValue(humanBeingDTO.getHasToothpick()));
        humanBeing.setImpactSpeed(FieldValidationUtil.getFloatFieldValue(humanBeingDTO.getImpactSpeed()));
        humanBeing.setSoundtrackName(FieldValidationUtil.getStringValue(humanBeingDTO.getSoundtrackName()));
        humanBeing.setWeaponType(FieldValidationUtil.getWeaponTypeValue(humanBeingDTO.getWeaponType()));
        humanBeing.setMood(FieldValidationUtil.getMoodValue(humanBeingDTO.getMood()));
        humanBeing.setCar(carMapper.mapCarDTOToCar(humanBeingDTO.getCar()));
        return humanBeing;
    }

    public HumanBeingDTO mapHumanBeingToHumanBeingDTO(HumanBeing humanBeing) {
        HumanBeingDTO humanBeingDTO = new HumanBeingDTO();
        humanBeingDTO.setId(String.valueOf(humanBeing.getId()));
        humanBeingDTO.setName(String.valueOf(humanBeing.getName()));
        humanBeingDTO.setCoordinates(coordinatesMapper.mapCoordinatesToCoordinatesDTO(humanBeing.getCoordinates()));
        humanBeingDTO.setRealHero(String.valueOf(humanBeing.getRealHero()));
        humanBeingDTO.setHasToothpick(String.valueOf(humanBeing.getHasToothpick()));
        humanBeingDTO.setImpactSpeed(String.valueOf(humanBeing.getImpactSpeed()));
        humanBeingDTO.setSoundtrackName(String.valueOf(humanBeing.getSoundtrackName()));
        humanBeingDTO.setWeaponType(String.valueOf(humanBeing.getWeaponType()));
        humanBeingDTO.setMood(String.valueOf(humanBeing.getMood()));
        humanBeingDTO.setCar(carMapper.mapCarToCarDTO(humanBeing.getCar()));
        return humanBeingDTO;
    }

    public List<HumanBeingDTO> mapHumanBeingListToHumanBeingDTOList(List<HumanBeing> humanBeingList) {
        ArrayList<HumanBeingDTO> humanBeingDTOList = new ArrayList<>();
        for (HumanBeing humanBeing : humanBeingList) {
            humanBeingDTOList.add(mapHumanBeingToHumanBeingDTO(humanBeing));
        }
        return humanBeingDTOList;
    }
}
