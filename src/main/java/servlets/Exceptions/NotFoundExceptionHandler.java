package servlets.Exceptions;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "NotFoundExceptionHandler", value = "/notFoundExceptionHandler")
public class NotFoundExceptionHandler extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(404);
        Throwable error = (Throwable) req.getAttribute("javax.servlet.error.exception");
        resp.getWriter().println(error.getMessage());
    }
}
