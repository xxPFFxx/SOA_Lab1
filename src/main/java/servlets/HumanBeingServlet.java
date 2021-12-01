package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.HumanBeingDTO;
import dto.PagedHumanBeingList;
import dto.dtoList.HumanBeingDTOList;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import mapper.HumanBeingMapper;
import models.HumanBeing;
import net.bytebuddy.implementation.bytecode.Throw;
import repository.implementation.CrudRepositoryImplementation;
import util.UrlParametersUtil;
import validation.EntityValidator;

import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        cors(response);
        String perPage = UrlParametersUtil.getField(request, "pageSize");
        String curPage = UrlParametersUtil.getField(request, "pageNumber");
        String sortBy = UrlParametersUtil.getField(request, "orderBy");
        String filterBy = UrlParametersUtil.getField(request, "filterBy");

        String pathInfo = request.getPathInfo();
        String id = null;
        if (pathInfo != null)
            id = pathInfo.substring(1);

        if (id != null) {
            String finalId = id;
            try {
                int intId = Integer.parseInt(id);
                if (intId <= 0) throw new BadRequestException("Bad format of id: " + id + ", should be natural number (1,2,...)");
                HumanBeing humanBeing = (repository.findById(intId)).orElseThrow(() -> new NotFoundException("humanBeing with id = " + finalId + " does not exist"));
                HumanBeingDTO humanBeingDTO = humanBeingMapper.mapHumanBeingToHumanBeingDTO(humanBeing);
                response.getWriter().write(gson.toJson(humanBeingDTO));
                return;
            }catch (NumberFormatException e){
                throw new BadRequestException("Bad format of id: " + id + ", should be natural number (1,2,...)");
            }catch (NoResultException e){
                throw new NotFoundException("No HumanBeing with id " + id);
            }
        }

        PagedHumanBeingList pagedHumanBeingList = repository.findAll(perPage, curPage, sortBy, filterBy);
        HumanBeingDTOList dto = new HumanBeingDTOList((humanBeingMapper.mapHumanBeingListToHumanBeingDTOList(pagedHumanBeingList.getHumanBeingList())), pagedHumanBeingList.getCount());
        response.getWriter().write(gson.toJson(dto));
        repository.clearEntityManager();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cors(response);
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HumanBeingDTO humanBeingDTO = gson.fromJson(requestBody, HumanBeingDTO.class);
        HumanBeing humanBeingToPersist = humanBeingMapper.mapHumanBeingDTOToHumanBeing(humanBeingDTO);
        entityValidator.validateHumanBeing(humanBeingToPersist);
        entityValidator.validateCoordinates(humanBeingToPersist.getCoordinates());
        entityValidator.validateCar(humanBeingToPersist.getCar());
        repository.save(humanBeingToPersist);

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cors(response);
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HumanBeingDTO humanBeingDTO = gson.fromJson(requestBody, HumanBeingDTO.class);
        HumanBeing humanBeingToUpdate = humanBeingMapper.mapHumanBeingDTOToHumanBeing(humanBeingDTO);
        entityValidator.validateHumanBeing(humanBeingToUpdate);
        entityValidator.validateCoordinates(humanBeingToUpdate.getCoordinates());
        entityValidator.validateCar(humanBeingToUpdate.getCar());
        String pathInfo = request.getPathInfo();
        String id = null;
        if (pathInfo != null)
            id = pathInfo.substring(1);

        if (id != null) {
            humanBeingToUpdate.setId(Long.parseLong(id));
            repository.update(humanBeingToUpdate);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cors(response);
        String pathInfo = request.getPathInfo();
        String humanBeingId = null;
        if (pathInfo != null)
            humanBeingId = pathInfo.substring(1);
        String finalHumanBeingId = humanBeingId;
        HumanBeing humanBeing = (repository.findById(Integer.parseInt(humanBeingId))).orElseThrow(() -> new NotFoundException("humanBeing with id = " + finalHumanBeingId + " does not exist"));
        repository.deleteById(Integer.parseInt(humanBeingId));

    }
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cors(response);
    }

    protected void cors(HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, HEAD, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.addHeader("Access-Control-Allow-Credentials", "true");
    }
}
