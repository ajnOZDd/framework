package etu2085.framework.servlet;

import java.io.IOException;
import java.util.List;



import etu2085.framework.controller.AnnotationController;
import etu2085.framework.utils.AnnotationUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {

       private static final long serialVersionUID = 1L;
    private String controllerPackage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Récupérer le nom du package des contrôleurs depuis le web.xml
        this.controllerPackage = config.getInitParameter("controllerPackage");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Récupérer l'URL de la requête
        String currentUrl = request.getRequestURI();

        // Afficher l'URL dans la réponse
        response.getWriter().println("Vous êtes sur l'URL: " + currentUrl);

        // Vérifier si les contrôleurs ont déjà été scannés
        List<Class<?>> annotatedClasses = AnnotationUtils.getAnnotatedClasses(this.controllerPackage, AnnotationController.class);

        // Afficher la liste des contrôleurs
        response.getWriter().println("Contrôleurs annotés avec @AnnotationController :");
        for (Class<?> clazz : annotatedClasses) {
            response.getWriter().println(clazz.getName());
        }
    }
        
      @Override
      protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
          // TODO Auto-generated method stub
          super.doGet(req, resp);
      }
    }

