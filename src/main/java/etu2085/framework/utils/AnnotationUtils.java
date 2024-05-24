package etu2085.framework.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AnnotationUtils {

    public static List<Class<?>> getAnnotatedClasses(String packageName, Class<? extends Annotation> annotationClass) {
        try {
            // Essayer de charger les classes depuis le ClassLoader du thread courant
            return getAnnotatedClasses(Thread.currentThread().getContextClassLoader(), packageName, annotationClass);
        } catch (ClassNotFoundException e) {
            try {
                // Si cela échoue, essayer de charger les classes depuis le ClassLoader de cette classe
                return getAnnotatedClasses(AnnotationUtils.class.getClassLoader(), packageName, annotationClass);
            } catch (ClassNotFoundException e2) {
                e2.printStackTrace();
                return new ArrayList<>();
            }
        }
    }

    private static List<Class<?>> getAnnotatedClasses(ClassLoader classLoader, String packageName, Class<? extends Annotation> annotationClass) throws ClassNotFoundException {
        List<Class<?>> annotatedClasses = new ArrayList<>();

        // Charger les classes du package spécifié
        Class<?>[] classes = getClasses(classLoader, packageName);

        // Parcourir les classes et vérifier la présence de l'annotation
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                annotatedClasses.add(clazz);
            }
        }

        return annotatedClasses;
    }

    private static Class<?>[] getClasses(ClassLoader classLoader, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        String path = packageName.replace('.', '/');
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            e.printStackTrace();
            return new Class<?>[0];
        }

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            System.out.println("Package trouvé : " + resource.getPath());
            if (resource.getProtocol().equals("file")) {
                // Traiter le cas des répertoires
                File directory = new File(resource.getFile());
                classes.addAll(findClasses(directory, packageName));
            } else if (resource.getProtocol().equals("jar")) {
                // Traiter le cas des fichiers JAR
                try {
                    JarURLConnection connection = (JarURLConnection) resource.openConnection();
                    JarFile jarFile = connection.getJarFile();
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (entry.getName().endsWith(".class") && entry.getName().startsWith(path)) {
                            String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
                            System.out.println("Classe trouvée : " + className);
                            classes.add(classLoader.loadClass(className));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return classes.toArray(new Class[0]);
    }

    private static List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    System.out.println("Classe trouvée : " + className);
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }
}