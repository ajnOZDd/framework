package utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import annotation.AnnotationController;
import annotation.Get;
import annotation.Param;
import jakarta.servlet.http.HttpServletRequest;
import models.Mapping;

public class AnnotationUtils {

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

  
    public static Object[] getMethodParameterValues(Method method, HttpServletRequest request) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] parameterValues = new Object[method.getParameterCount()];

        for (int i = 0; i < parameterAnnotations.length; i++) {
            String paramName = method.getParameters()[i].getName(); // Nom par défaut basé sur le nom de la variable

            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Param) {
                    Param customParam = (Param) annotation;
                    paramName = customParam.name(); // Si l'annotation @Param est présente, utilisez son nom
                    break;
                }
            }

            parameterValues[i] = convertParameter(request.getParameter(paramName), method.getParameterTypes()[i]);
        }

        return parameterValues;
    }

    private static Object convertParameter(String paramValue, Class<?> paramType) {
        // Conversion de paramValue en paramType approprié, comme précédemment défini
        if (paramValue == null) {
            return null;
        }
        if (paramType == String.class) {
            return paramValue;
        } else if (paramType == int.class || paramType == Integer.class) {
            return Integer.parseInt(paramValue);
        } else if (paramType == long.class || paramType == Long.class) {
            return Long.parseLong(paramValue);
        } else if (paramType == double.class || paramType == Double.class) {
            return Double.parseDouble(paramValue);
        } else if (paramType == float.class || paramType == Float.class) {
            return Float.parseFloat(paramValue);
        } else if (paramType == boolean.class || paramType == Boolean.class) {
            return Boolean.parseBoolean(paramValue);
        }
        return null;
    }

    private static List<Class<?>> findClassesWithAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        List<Class<?>> classes = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            java.net.URL resource = classLoader.getResource(path);
            File directory = new File(resource.getFile());
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(annotationClass)) {
                        classes.add(clazz);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
}
