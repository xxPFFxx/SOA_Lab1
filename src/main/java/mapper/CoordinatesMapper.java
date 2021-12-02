package mapper;

import dto.CoordinatesDTO;
import exceptions.BadRequestException;
import models.Coordinates;
import util.FieldValidationUtil;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesMapper {

    public Coordinates mapCoordinatesDTOToCoordinates(CoordinatesDTO coordinatesDTO) {
        try {
            Coordinates coordinates = new Coordinates();
            coordinates.setId(FieldValidationUtil.getLongFieldValue(coordinatesDTO.getId()));
            coordinates.setX(FieldValidationUtil.getIntegerFieldValue(coordinatesDTO.getX()));
            coordinates.setY(FieldValidationUtil.getLongFieldValue(coordinatesDTO.getY()));
            return coordinates;
        }
        catch (NullPointerException e) {
            throw new BadRequestException("Bad format of JSON body");
        }
    }

    public CoordinatesDTO mapCoordinatesToCoordinatesDTO(Coordinates coordinates) {
        CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
        coordinatesDTO.setId(String.valueOf(coordinates.getId()));
        coordinatesDTO.setX(String.valueOf(coordinates.getX()));
        coordinatesDTO.setY(String.valueOf(coordinates.getY()));
        return coordinatesDTO;
    }

    public List<CoordinatesDTO> mapCoordinatesListToCoordinatesDTOList(List<Coordinates> coordinatesList) {
        ArrayList<CoordinatesDTO> coordinatesDTOList = new ArrayList<>();
        for (Coordinates coordinates : coordinatesList) {
            coordinatesDTOList.add(mapCoordinatesToCoordinatesDTO(coordinates));
        }
        return coordinatesDTOList;
    }
}