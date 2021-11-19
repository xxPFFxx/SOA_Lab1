package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.HumanBeingDTO;
import dto.PagedHumanBeingList;
import dto.dtoList.HumanBeingDTOList;
import exceptions.EntityIsNotValidException;
import mapper.CoordinatesMapper;
import mapper.HumanBeingMapper;
import models.HumanBeing;
import repository.implementation.CrudRepositoryImplementation;
import util.UrlParametersUtil;
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
            HumanBeing humanBeing = (repository.findById(Integer.parseInt(id))).orElseThrow(() -> new EntityIsNotValidException("humanBeing with id = " + finalId + " does not exist"));
            HumanBeingDTOList dto = new HumanBeingDTOList(new ArrayList<>(), 1);
            List<HumanBeingDTO> dtoList = new ArrayList<>();
            dtoList.add(humanBeingMapper.mapHumanBeingToHumanBeingDTO(humanBeing));
            dto.setHumanBeingList(dtoList);
            response.getWriter().write(gson.toJson(dto));
            return;
        }

//        List<HumanBeing> humanBeing = repository.findAll();
//
//        HumanBeingDTOList dto = new HumanBeingDTOList(new ArrayList<>());
//        dto.setHumanBeingList(humanBeingMapper.mapHumanBeingListToHumanBeingDTOList(humanBeing));

        PagedHumanBeingList pagedHumanBeingList = repository.findAll(perPage, curPage, sortBy, filterBy);
        HumanBeingDTOList dto = new HumanBeingDTOList((humanBeingMapper.mapHumanBeingListToHumanBeingDTOList(pagedHumanBeingList.getHumanBeingList())), pagedHumanBeingList.getCount());
        response.getWriter().write(gson.toJson(dto));
        repository.clearEntityManager();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cors(response);
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HumanBeingDTOList humanBeingDTOList = gson.fromJson(requestBody, HumanBeingDTOList.class);
        HumanBeing humanBeingToPersist = humanBeingMapper.mapHumanBeingDTOToHumanBeing(humanBeingDTOList.getHumanBeingList().get(0));
        humanBeingToPersist.setCreationDate(LocalDateTime.now()); // TODO Не устанавливается дата создания в БД почему-то
        entityValidator.validateHumanBeing(humanBeingToPersist);
        repository.save(humanBeingToPersist);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cors(response);
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

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cors(response);
        String pathInfo = request.getPathInfo();
        String humanBeingId = null;
        if (pathInfo != null)
            humanBeingId = pathInfo.substring(1);
        String finalHumanBeingId = humanBeingId;
        HumanBeing humanBeing = (repository.findById(Integer.parseInt(humanBeingId))).orElseThrow(() -> new EntityIsNotValidException("humanBeing with id = " + finalHumanBeingId + " does not exist"));
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
