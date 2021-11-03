package servlets;

import models.Coordinates;
import models.HumanBeing;
import repository.implementation.CrudRepositoryImplementation;
import validation.EntityValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/human-beings/*")
public class HumanBeingServlet extends HttpServlet {
    private CrudRepositoryImplementation<HumanBeing> repository;
    private EntityValidator entityValidator;


    @Override
    public void init() throws ServletException {
        repository = new CrudRepositoryImplementation<>(HumanBeing.class);
        entityValidator = new EntityValidator();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO Реализовать логику GET-запроса
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO Реализовать логику POST-запроса
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO Реализовать логику PUT-запроса
    }
}
