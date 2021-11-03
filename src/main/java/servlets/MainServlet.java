package servlets;

import com.google.gson.Gson;
import models.*;
import repository.implementation.CrudRepositoryImplementation;
import validation.EntityValidator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;

public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CrudRepositoryImplementation<HumanBeing> repository = new CrudRepositoryImplementation<>(HumanBeing.class);
//        Car car = new Car("Молния", Boolean.TRUE);
//        Coordinates coordinates = new Coordinates(200,3L);
//        EntityValidator entityValidator = new EntityValidator();
//        HumanBeing humanBeing = new HumanBeing("PFF", coordinates, Boolean.FALSE, true, Float.parseFloat("35.5"),
//                "Belupacito", WeaponType.AXE, Mood.CALM, car);
//        entityValidator.validateHumanBeing(humanBeing);
//        repository.save(humanBeing);
//        String locationId = req.getParameter("id");
//
//        sendAsJson(resp, repository.findById(Integer.parseInt(locationId)).get());
    }

    private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {

        Gson gson = new Gson();

        response.setContentType("application/json");

        String res = gson.toJson(obj);

        PrintWriter out = response.getWriter();

        out.print(res);
        out.flush();
    }
}
