package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Mapping;
import models.ModelView;
import utils.AnnotationUtils;

public class FrontController extends HttpServlet {

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

                    Method[] methods = controllerClass.getDeclaredMethods();
                    Method method = null;

                    for (Method m : methods) {
                        if (m.getName().equals(mapping.getMethodName())) {
                            method = m;
                            break;
                        }
                    }

                    if (method == null) {
                        out.println("Méthode non trouvée : " + mapping.getMethodName());
                        return;
                    }

                    Object[] params = AnnotationUtils.getMethodParameterValues(method, request);
                    Object result = method.invoke(controllerInstance, params);

                    if (result instanceof String) {
                        out.println((String) result);
                    } else if (result instanceof ModelView) {
                        ModelView modelView = (ModelView) result;
                        String url = modelView.getUrl();
                        HashMap<String, Object> data = modelView.getData();

                        if (data != null) {
                            for (String key : data.keySet()) {
                                request.setAttribute(key, data.get(key));
                            }
                        }

                        String realPath = getServletContext().getRealPath(url);
                        if (realPath != null) {
                            request.getRequestDispatcher(url).forward(request, response);
                        } else {
                            out.println("Erreur : Impossible de trouver la JSP " + url);
                        }
                    } else {
                        out.println("Type de retour non reconnu");
                    }
                } catch (Exception e) {
                    out.println("Erreur lors de l'appel de la méthode : " + e.getClass().getName());
                    out.println("Message : " + (e.getMessage() != null ? e.getMessage() : "Aucun message"));
                    e.printStackTrace(out);
                }
            } else {
                out.println("URL non trouvée : " + requestUrl);
            }
        }
    }
}
