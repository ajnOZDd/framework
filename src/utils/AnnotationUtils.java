package utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotation.AnnotationController;
import annotation.Get;
import models.Mapping;

public class AnnotationUtils {
    private static final Map<String, List<Class<?>>> scannedClasses = new HashMap<>();

    public static List<Class<?>> findClassesInPackage(String packageName) {
        if (scannedClasses.containsKey(packageName)) {
            return scannedClasses.get(packageName);
        }

        List<Class<?>> classes = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                if (directory.isDirectory()) {
                    for (File file : directory.listFiles()) {
                        if (file.getName().endsWith(".class")) {
                            String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                            Class<?> clazz = Class.forName(className);
                            if (!Modifier.isAbstract(clazz.getModifiers())) {
                                classes.add(clazz);
                            }
                        }
                    }
                }
            }
            scannedClasses.put(packageName, classes);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static List<Class<?>> findClassesWithAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        List<Class<?>> classes = findClassesInPackage(packageName);
        List<Class<?>> annotatedClasses = new ArrayList<>();
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                annotatedClasses.add(clazz);
            }
        }
        return annotatedClasses;
    }

    public static List<String> getControllerNames(String packageName, Class<? extends Annotation> annotationClass) {
        List<String> controllerNames = new ArrayList<>();
        List<Class<?>> controllerClasses = findClassesWithAnnotation(packageName, annotationClass);
        for (Class<?> controllerClass : controllerClasses) {
            controllerNames.add(controllerClass.getSimpleName());
        }
        return controllerNames;
    }

   public static HashMap<String, Mapping> createUrlMappings(String packageName) {
    HashMap<String, Mapping> urlMappings = new HashMap<>();
    List<Class<?>> controllerClasses = findClassesWithAnnotation(packageName, AnnotationController.class);

    for (Class<?> controllerClass : controllerClasses) {
        String controllerName = controllerClass.getSimpleName();
        Method[] methods = controllerClass.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Get.class)) {
                Get getAnnotation = method.getAnnotation(Get.class);
                String url = getAnnotation.value();
                Mapping mapping = new Mapping(controllerName, method.getName());
                urlMappings.put(url, mapping);
            }
        }
    }

    return urlMappings;
  }

}