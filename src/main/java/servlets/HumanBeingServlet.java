package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dto.HumanBeingDTO;
import dto.PagedHumanBeingList;
import dto.dtoList.HumanBeingDTOList;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import mapper.HumanBeingMapper;
import models.HumanBeing;
import repository.implementation.CrudRepositoryImplementation;
import services.HumanBeingService;
import util.UrlParametersUtil;
import validation.EntityValidator;

import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/human-beings/*")
public class HumanBeingServlet extends HttpServlet {
    private HumanBeingService humanBeingService;

    @Override
    public void init() throws ServletException {
        humanBeingService = new HumanBeingService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cors(response);
        String perPage = UrlParametersUtil.getField(request, "pageSize");
        String curPage = UrlParametersUtil.getField(request, "pageNumber");
        String sortBy = UrlParametersUtil.getField(request, "orderBy");
        String filterBy = UrlParametersUtil.getField(request, "filterBy");

        String pathInfo = request.getPathInfo();
        humanBeingService.getHumanBeing(pathInfo, response, perPage, curPage, sortBy, filterBy);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cors(response);
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        humanBeingService.saveHumanBeing(requestBody);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            cors(response);
            String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            String pathInfo = request.getPathInfo();
            humanBeingService.updateHumanBeing(requestBody, pathInfo);
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cors(response);
        String pathInfo = request.getPathInfo();
        humanBeingService.deleteHumanBeing(pathInfo);
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
