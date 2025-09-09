package utils;

import annotation.AnnotationController;
import annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationUtils {

    /**
     * Récupère tous les mappings URL -> Method des contrôleurs
     */
    public static Map<String, Method> getUrlMappings(String packageName) {
        Map<String, Method> mappings = new HashMap<>();
        
        try {
            List<Class<?>> controllers = getAnnotatedControllers(packageName);
            for (Class<?> controller : controllers) {
                Method[] methods = controller.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        String url = method.getAnnotation(GetMapping.class).value();
                        mappings.put(url, method);
                        System.out.println("Mapping: " + url + " -> " + controller.getSimpleName() + "." + method.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mappings;
    }

    /**
     * Trouve toutes les classes annotées @AnnotationController
     */
    public static List<Class<?>> getAnnotatedControllers(String packageName) {
        List<Class<?>> controllers = new ArrayList<>();
        try {
            String path = packageName.replace('.', '/');
            URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
            if (resource == null) return controllers;

            File directory = new File(resource.toURI());
            if (!directory.exists()) return controllers;

            for (String fileName : directory.list()) {
                if (fileName.endsWith(".class")) {
                    String className = packageName + "." + fileName.replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(AnnotationController.class)) {
                        controllers.add(clazz);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return controllers;
    }

    /**
     * Invoque une méthode de contrôleur
     */
    public static Object invokeController(Method method) throws Exception {
        Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
        return method.invoke(instance);
    }

    /**
     * Résout le chemin d'une vue
     */
    public static String resolveView(String viewName) {
        if (viewName.startsWith("/")) return viewName;
        if (viewName.startsWith("views/")) return "/" + viewName;
        return "/views/" + viewName;
    }

    /**
     * Traite le résultat d'un contrôleur et envoie la réponse
     */
    public static void handleResult(HttpServletRequest request, HttpServletResponse response, Object result) 
            throws IOException, ServletException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        if (result instanceof String) {
            // Réponse texte
            response.getWriter().println(result);
            
        } else if (result instanceof ModelView) {
            // Forward vers une vue
            ModelView mv = (ModelView) result;
            String viewPath = resolveView(mv.getUrl());
            
            // Passer les données
            for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
            
            // Forward
            RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
            if (dispatcher != null) {
                dispatcher.forward(request, response);
            } else {
                response.getWriter().println("❌ Vue introuvable: " + viewPath);
            }
        } else {
            response.getWriter().println("❌ Type de retour non supporté: " + result);
        }
    }

    /**
     * Envoie une erreur 404
     */
    public static void sendNotFound(HttpServletResponse response, String path, Map<String, Method> mappings) 
            throws IOException {
        response.setStatus(404);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<h1>Erreur 404: Aucun mapping trouvé pour " + path + "</h1>");
        out.println("<p>Mappings: " + mappings.keySet() + "</p>");
    }

    /**
     * Vérifie si c'est une ressource statique
     */
    public static boolean isStaticResource(String path) {
        return path.startsWith("/views/") || path.endsWith(".jsp") || path.endsWith(".css") || path.endsWith(".js");
    }
}