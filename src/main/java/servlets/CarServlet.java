package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CarDTO;
import dto.dtoList.CarDTOList;
import exceptions.NotFoundException;
import mapper.CarMapper;
import models.Car;
import repository.implementation.CrudRepositoryImplementation;
import validation.EntityValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/cars/*")
public class CarServlet extends HttpServlet {
    private CrudRepositoryImplementation<Car> repository;
    private EntityValidator entityValidator;
    private CarMapper carMapper;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        repository = new CrudRepositoryImplementation<>(Car.class);
        entityValidator = new EntityValidator();
        carMapper = new CarMapper();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String id = null;
        if (pathInfo != null)
            id = pathInfo.substring(1);

        if (id != null) {
            String finalId = id;
            Car car = (repository.findById(Integer.parseInt(id))).orElseThrow(() -> new NotFoundException("car with id = " + finalId + " does not exist"));
            CarDTOList dto = new CarDTOList(new ArrayList<>());
            List<CarDTO> dtoList = new ArrayList<>();
            dtoList.add(carMapper.mapCarToCarDTO(car));
            dto.setCarList(dtoList);
            response.getWriter().write(gson.toJson(dto));
            return;
        }

        List<Car> car = repository.findAll();

        CarDTOList dto = new CarDTOList(new ArrayList<>());
        dto.setCarList(carMapper.mapCarListToCarDTOList(car));

        response.getWriter().write(gson.toJson(dto));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        CarDTOList carDTOList = gson.fromJson(requestBody, CarDTOList.class);
        Car carToPersist = carMapper.mapCarDTOToCar(carDTOList.getCarList().get(0));
        entityValidator.validateCar(carToPersist);
        repository.save(carToPersist);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        CarDTOList carDTOList = gson.fromJson(requestBody, CarDTOList.class);
        Car carToUpdate = carMapper.mapCarDTOToCar(carDTOList.getCarList().get(0));
        String pathInfo = request.getPathInfo();
        String id = null;
        if (pathInfo != null)
            id = pathInfo.substring(1);

        if (id != null) {
            carToUpdate.setId(Long.parseLong(id));
            entityValidator.validateCar(carToUpdate);
            repository.update(carToUpdate);
        }
    }
}
