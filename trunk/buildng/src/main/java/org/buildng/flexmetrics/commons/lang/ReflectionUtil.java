package org.buildng.flexmetrics.commons.lang;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Hilfsklasse mit Komfortfunktionen beim Umgang mit Reflection.
 * 
 * @author TWIEGER
 */
public class ReflectionUtil {
    
    /**
     * private ctor: no instances.
     */
    private ReflectionUtil() {
        
    }

    /**
     * Liefert alle Interfaces die die übergebene Klasse und alle ihre Superklassen implementieren.
     * 
     * @param pClass die Klasse deren Interfaces zurückgeliefert werden sollen.
     * @return alle Interfaces die die übergebene Klasse und alle ihre Superklassen implementieren.
     */
    public static Class[] getAllInterfaces(Class<? extends Object> pClass) {
        Set<Class> allInterfaces = new HashSet<Class>();
        Class clazz = pClass;
        do {
            allInterfaces.addAll(Arrays.asList(clazz.getInterfaces()));
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return allInterfaces.toArray(new Class[allInterfaces.size()]);
    }
}
