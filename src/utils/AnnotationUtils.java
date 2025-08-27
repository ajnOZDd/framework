package utils;

import annotation.AnnotationController;
import annotation.GetMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationUtils {

    /**
     * Cherche toutes les classes dans un package donné qui ont l'annotation @AnnotationController
     */
    public static List<Class<?>> getAnnotatedControllers(String packageName) {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        try {
            String path = packageName.replace('.', '/');
            URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
            if (resource == null) {
                System.err.println("Package introuvable: " + packageName);
                return annotatedClasses;
            }

            File directory = new File(resource.toURI());
            if (!directory.exists()) return annotatedClasses;

            for (String fileName : directory.list()) {
                if (fileName.endsWith(".class")) {
                    String className = packageName + "." + fileName.replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(AnnotationController.class)) {
                        annotatedClasses.add(clazz);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return annotatedClasses;
    }

    public static Map<String, Method> getUrlMappings(String packageName) {
        Map<String, Method> mappings = new HashMap<>();
        try {
            String path = packageName.replace('.', '/');
            URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
            if (resource == null) {
                System.err.println("Package introuvable: " + packageName);
                return mappings;
            }

            File directory = new File(resource.toURI());
            if (!directory.exists()) return mappings;

            for (String fileName : directory.list()) {
                if (fileName.endsWith(".class")) {
                    String className = packageName + "." + fileName.replace(".class", "");
                    Class<?> clazz = Class.forName(className);

                    if (clazz.isAnnotationPresent(AnnotationController.class)) {
                        for (Method method : clazz.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(GetMapping.class)) {
                                String url = method.getAnnotation(GetMapping.class).value();
                                mappings.put(url, method);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mappings;
    }
}
