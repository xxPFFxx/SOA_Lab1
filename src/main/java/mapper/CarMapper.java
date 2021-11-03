package mapper;

import dto.CarDTO;
import models.Car;
import util.FieldValidationUtil;

import java.util.ArrayList;
import java.util.List;

public class CarMapper {
    public Car mapCarDTOToCar(CarDTO carDTO) {
        Car car = new Car();
        car.setId(FieldValidationUtil.getLongFieldValue(carDTO.getId()));
        car.setName(FieldValidationUtil.getStringValue(carDTO.getName()));
        car.setCool(FieldValidationUtil.getBooleanFieldValue(carDTO.getCool()));
        return car;
    }

    public CarDTO mapCarToCarDTO(Car car) {
        CarDTO carDTO = new CarDTO();
        carDTO.setId(String.valueOf(car.getId()));
        carDTO.setName(String.valueOf(car.getName()));
        carDTO.setCool(String.valueOf(car.getCool()));
        return carDTO;
    }

    public List<CarDTO> mapCarListToCarDTOList(List<Car> carList) {
        ArrayList<CarDTO> carDTOList = new ArrayList<>();
        for (Car car : carList) {
            carDTOList.add(mapCarToCarDTO(car));
        }
        return carDTOList;
    }
}
