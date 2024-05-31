package etu2085.framework.servlet;

import java.io.IOException;
import java.util.List;
import etu2085.framework.controller.AnnotationController;
import etu2085.framework.utils.AnnotationUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {

    private List<Class<?>> annotatedClasses;


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String currentUrl = request.getRequestURI();

        // Afficher l'URL dans la réponse
        response.getWriter().println("Vous êtes sur l'URL: " + currentUrl);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
    
}