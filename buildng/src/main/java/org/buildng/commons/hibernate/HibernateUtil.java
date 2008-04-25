package org.buildng.commons.hibernate;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;

/**
 * Stellt eine HibernateSession pro Thread zur Verfügung.
 */
public class HibernateUtil {
    //--------------------------------------------------------------------------  
    // class variables
    //--------------------------------------------------------------------------

    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(HibernateUtil.class);

    /** Hibernate Configuration */
    private static Configuration sfConfiguration;

    /** Hibernate SessionFactory */
    private static SessionFactory sfSessionFactory;

    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    /**
     * Keine Instanzen zulassen.
     */
    private HibernateUtil() {
    }

    
    
    //--------------------------------------------------------------------------  
    // class methods
    //--------------------------------------------------------------------------

    /**
     * Liefert eine SessionFactory zurück, die über eine AnnotationConfiguration
     * angelegt wurde. Alternativ kann mit {@see #bindSessionFactory(SessionFactory)}
     * eine alternative SessionFactory definiert werden, die dann hier
     * zurckgeliefert wird.
     * 
     * @return Die Hibernate SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        if (sfSessionFactory!=null) {
            return sfSessionFactory;
        }
        try {
            sfConfiguration = new AnnotationConfiguration();
            sfConfiguration.configure();
            sfSessionFactory = sfConfiguration.buildSessionFactory();
        } catch (RuntimeException ex) {
            LOGGER.error("Building SessionFactory failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
        return sfSessionFactory;
    }
    
    /** 
     * @return Die aktuelle Hibernate-Session.
     */
    public static Session getCurrentSession() {
        return getSessionFactory().getCurrentSession();
    }
    
    public static void save(Object pObject) {
        getCurrentSession().save(pObject);
    }
    

    public static void evict(Collection pList) {
        for (Object element : pList) {
            getCurrentSession().evict(element);
        }
    }

    /**
     * Gibt den SQL Dialect für die eingesetzte Datenbank zurück.
     * 
     * @return Dialect der SQL Dialect der Datenbank.
     */
    public static Dialect getDialect() {
        return Dialect.getDialect(sfConfiguration.getProperties());
    }
}
