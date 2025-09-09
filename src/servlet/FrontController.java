package servlet;

import utils.AnnotationUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * FrontController minimal - juste des appels aux utilitaires
 */
public class FrontController extends HttpServlet {

    private Map<String, Method> mappings;

    @Override
    public void init() throws ServletException {
        String packageName = getServletConfig().getInitParameter("controllers-package");
        if (packageName == null) packageName = "controllers";
        
        mappings = AnnotationUtils.getUrlMappings(packageName);
        System.out.println("✅ " + mappings.size() + " mappings chargés");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        String path = request.getPathInfo();
        if (path == null) path = "/";

        // Ressources statiques
        if (AnnotationUtils.isStaticResource(path)) {
            getServletContext().getNamedDispatcher("default").forward(request, response);
            return;
        }

        // Pas de mapping trouvé
        if (!mappings.containsKey(path)) {
            AnnotationUtils.sendNotFound(response, path, mappings);
            return;
        }

        try {
            // Invoquer le contrôleur et traiter le résultat
            Method method = mappings.get(path);
            Object result = AnnotationUtils.invokeController(method);
            AnnotationUtils.handleResult(request, response, result);
            
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}