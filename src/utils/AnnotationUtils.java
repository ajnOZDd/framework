package utils;

import annotation.AnnotationController;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
}
