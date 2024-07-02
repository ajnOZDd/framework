package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ModuleLayer.Controller;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import annotation.AnnotationController;
import annotation.Get;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Mapping;
import models.ModelView;
import utils.AnnotationUtils;


public class FrontController extends HttpServlet {

    private List<String> controllerNames = new ArrayList<>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
      
    }
private void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");
    try (PrintWriter out = response.getWriter()) {
        HashMap<String, Mapping> urlMappings = AnnotationUtils.createUrlMappings("controllers");

        String requestUrl = request.getRequestURI().substring(request.getContextPath().length());

        if (urlMappings.containsKey(requestUrl)) {
            Mapping mapping = urlMappings.get(requestUrl);
            
            try {
                Class<?> controllerClass = Class.forName("controllers." + mapping.getClassName());
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
                Method method = controllerClass.getDeclaredMethod(mapping.getMethodName());
                Object result = method.invoke(controllerInstance);
                
                if (result instanceof String) {
                    out.println((String) result);
                } else if (result instanceof ModelView) {
                    ModelView modelView = (ModelView) result;
                    String url = modelView.getUrl();
                    HashMap<String, Object> data = modelView.getData();
                    
                    for (String key : data.keySet()) {
                        request.setAttribute(key, data.get(key));
                    }
                    
                    request.getRequestDispatcher(url).forward(request, response);
                } else {
                    out.println("Type de retour non reconnu");
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                out.println("Erreur lors de l'appel de la méthode : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            out.println("URL non trouvée : " + requestUrl);
        }
    }
}
}

    

    

  
