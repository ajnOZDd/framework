package etu2085.framework.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import jakarta.servlet.ServletContext;

public class AnnotationUtils {
    public static List<Class<?>> getAnnotatedClasses(ServletContext servletContext, String packageName, Class<? extends Annotation> annotation) {
        if (packageName != null) {
            List<Class<?>> classes;
            try {
                classes = getClasses(servletContext, packageName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Impossible de trouver les classes dans le package " + packageName, e);
            }
            List<Class<?>> annotatedClasses = new ArrayList<>();
    
            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(annotation)) {
                    annotatedClasses.add(clazz);
                }
            }
    
            return annotatedClasses;
        } else {
            throw new IllegalArgumentException("Le paramètre 'packageName' ne peut pas être null.");
        }
    }
    public static List<Class<?>> getClasses(ServletContext servletContext, String packageName) throws ClassNotFoundException {
        String packageNameFormatted = packageName.replace('.', '/');
        URL resource = null;
        try {
            resource = servletContext.getResource(packageNameFormatted);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
        if (resource != null) { 
            List<Class<?>> classes = new ArrayList<>();
    
            File dir = new File(resource.getFile());
    
            if (dir.isDirectory()) {
                for (File file : Objects.requireNonNull(dir.listFiles())) {
                    if (file.getName().endsWith(".class")) {
                        String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                        classes.add(servletContext.getClassLoader().loadClass(className));
                    }
                }
            } 
            return classes;
        } else {
            throw new ClassNotFoundException("Impossible de trouver les classes dans le package " + packageName);
        }
    }
}