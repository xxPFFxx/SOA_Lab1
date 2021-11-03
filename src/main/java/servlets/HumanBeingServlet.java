package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.HumanBeingDTO;
import dto.dtoList.HumanBeingDTOList;
import exceptions.EntityIsNotValidException;
import mapper.CoordinatesMapper;
import mapper.HumanBeingMapper;
import models.HumanBeing;
import repository.implementation.CrudRepositoryImplementation;
import validation.EntityValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/human-beings/*")
public class HumanBeingServlet extends HttpServlet {
    private CrudRepositoryImplementation<HumanBeing> repository;
    private EntityValidator entityValidator;
    private HumanBeingMapper humanBeingMapper;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        repository = new CrudRepositoryImplementation<>(HumanBeing.class);
        entityValidator = new EntityValidator();
        humanBeingMapper = new HumanBeingMapper();
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
            HumanBeing humanBeing = (repository.findById(Integer.parseInt(id))).orElseThrow(() -> new EntityIsNotValidException("humanBeing with id = " + finalId + " does not exist"));
            HumanBeingDTOList dto = new HumanBeingDTOList(new ArrayList<>());
            List<HumanBeingDTO> dtoList = new ArrayList<>();
            dtoList.add(humanBeingMapper.mapHumanBeingToHumanBeingDTO(humanBeing));
            dto.setHumanBeingList(dtoList);
            response.getWriter().write(gson.toJson(dto));
            return;
        }

        List<HumanBeing> humanBeing = repository.findAll();

        HumanBeingDTOList dto = new HumanBeingDTOList(new ArrayList<>());
        dto.setHumanBeingList(humanBeingMapper.mapHumanBeingListToHumanBeingDTOList(humanBeing));

        response.getWriter().write(gson.toJson(dto));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HumanBeingDTOList humanBeingDTOList = gson.fromJson(requestBody, HumanBeingDTOList.class);
        HumanBeing humanBeingToPersist = humanBeingMapper.mapHumanBeingDTOToHumanBeing(humanBeingDTOList.getHumanBeingList().get(0));
        humanBeingToPersist.setCreationDate(LocalDateTime.now());
        entityValidator.validateHumanBeing(humanBeingToPersist);
        repository.save(humanBeingToPersist);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HumanBeingDTOList humanBeingDTOList = gson.fromJson(requestBody, HumanBeingDTOList.class);
        HumanBeing humanBeingToUpdate = humanBeingMapper.mapHumanBeingDTOToHumanBeing(humanBeingDTOList.getHumanBeingList().get(0));
        entityValidator.validateHumanBeing(humanBeingToUpdate);
        String pathInfo = request.getPathInfo();
        String id = null;
        if (pathInfo != null)
            id = pathInfo.substring(1);

        if (id != null) {
            humanBeingToUpdate.setId(Long.parseLong(id));
            repository.update(humanBeingToUpdate);
        }
    }
}
