package servlets;

import com.google.gson.Gson;
import models.Coordinates;
import repository.implementation.CrudRepositoryImplementation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.getWriter().write("HELLO FROM SERVLET!");
        CrudRepositoryImplementation<Coordinates> repository = new CrudRepositoryImplementation<>(Coordinates.class);

        String locationId = req.getParameter("id");

        sendAsJson(resp, repository.findById(Integer.parseInt(locationId)).get());
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
