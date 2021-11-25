package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CoordinatesDTO;
import dto.dtoList.CoordinatesDTOList;
import exceptions.NotFoundException;
import mapper.CoordinatesMapper;
import models.Coordinates;
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

@WebServlet("/coordinates/*")
public class CoordinatesServlet extends HttpServlet {
    private CrudRepositoryImplementation<Coordinates> repository;
    private EntityValidator entityValidator;
    private CoordinatesMapper coordinatesMapper;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        repository = new CrudRepositoryImplementation<>(Coordinates.class);
        entityValidator = new EntityValidator();
        coordinatesMapper = new CoordinatesMapper();
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
            Coordinates coordinates = (repository.findById(Integer.parseInt(id))).orElseThrow(() -> new NotFoundException("coordinates with id = " + finalId + " does not exist"));
            CoordinatesDTOList dto = new CoordinatesDTOList(new ArrayList<>());
            List<CoordinatesDTO> dtoList = new ArrayList<>();
            dtoList.add(coordinatesMapper.mapCoordinatesToCoordinatesDTO(coordinates));
            dto.setCoordinatesList(dtoList);
            response.getWriter().write(gson.toJson(dto));
            return;
        }

        List<Coordinates> coordinates = repository.findAll();

        CoordinatesDTOList dto = new CoordinatesDTOList(new ArrayList<>());
        dto.setCoordinatesList(coordinatesMapper.mapCoordinatesListToCoordinatesDTOList(coordinates));

        response.getWriter().write(gson.toJson(dto));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        CoordinatesDTOList coordinatesDTOList = gson.fromJson(requestBody, CoordinatesDTOList.class);
        Coordinates coordinatesToPersist = coordinatesMapper.mapCoordinatesDTOToCoordinates(coordinatesDTOList.getCoordinatesList().get(0));
        entityValidator.validateCoordinates(coordinatesToPersist);
        repository.save(coordinatesToPersist);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        CoordinatesDTOList coordinatesDTOList = gson.fromJson(requestBody, CoordinatesDTOList.class);
        Coordinates coordinatesToUpdate = coordinatesMapper.mapCoordinatesDTOToCoordinates(coordinatesDTOList.getCoordinatesList().get(0));
        String pathInfo = request.getPathInfo();
        String id = null;
        if (pathInfo != null)
            id = pathInfo.substring(1);

        if (id != null) {
            coordinatesToUpdate.setId(Long.parseLong(id));
            entityValidator.validateCoordinates(coordinatesToUpdate);
            repository.update(coordinatesToUpdate);
        }
    }
}
