package servlets.Exceptions;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "BadRequestExceptionHandler", value = "/badRequestExceptionHandler")
public class BadRequestExceptionHandler extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(400);
        System.out.println(resp.getStatus());
        Throwable error = (Throwable) req.getAttribute("javax.servlet.error.exception");
        resp.getWriter().println(error.getMessage());
    }
}
