package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utils.AnnotationUtils;

import java.util.Map;

public class FrontController extends HttpServlet {

    private Map<String, Method> mappings;

    @Override
    public void init() throws ServletException {
        // Charger les mappings au démarrage
        mappings = AnnotationUtils.getUrlMappings("controllers");
        System.out.println("Mappings chargés: " + mappings.keySet());
    }

    @Override
    protected void doGet(HttpServletRequest rq, HttpServletResponse rs) 
            throws IOException, ServletException {
        processRequest(rq, rs);
    }

    @Override
    protected void doPost(HttpServletRequest rq, HttpServletResponse rs) 
            throws IOException, ServletException {
        processRequest(rq, rs);
    }

    public void processRequest(HttpServletRequest rq, HttpServletResponse rs) 
        throws IOException, ServletException {
        rs.setContentType("text/html;charset=UTF-8");

        String path = rq.getPathInfo(); // ex: /hello
        PrintWriter out = rs.getWriter();

        try {
            Method method = mappings.get(path);
            if (method != null) {
                Object controller = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                Object result = method.invoke(controller);
                if (result != null) {
                    out.println(result.toString());
                }
            } else {
                rs.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("<h1>404 - Page introuvable</h1>");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}
