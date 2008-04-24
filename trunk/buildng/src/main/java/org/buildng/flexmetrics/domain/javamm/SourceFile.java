package org.buildng.flexmetrics.domain.javamm;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.buildng.flexmetrics.domain.version.Version;


@Entity
public class SourceFile extends AbstractVersionedMetaData {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private Set<MetaClass>  fMetaClasses    = new HashSet<MetaClass>();

    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    SourceFile() {
    }
    
    public SourceFile(String pName, Version pVersion) {
        super(pName, pVersion);
    }
    
    
    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    @OneToMany(cascade = CascadeType.ALL)
    public Set<MetaClass> getMetaClasses() {
        return fMetaClasses;
    }

    void setMetaClasses(Set<MetaClass> pMetaClasses) {
        fMetaClasses = pMetaClasses;
    }
    
    public void addToMetaClasses(MetaClass pMetaClass) {
        fMetaClasses.add(pMetaClass);
    }

    
    //--------------------------------------------------------------------------  
    // SourceFile methods
    //--------------------------------------------------------------------------

    public MetaData getBestMatch(String pClassname, String pMethodname, int pLine) {
        MetaClass metaClass = getMetaClassByName(pClassname);
        if (metaClass==null) {
            return this;
        }
        MetaMethod  metaMethod = metaClass.getBestMatch(pMethodname, pLine);
        if (metaMethod==null) {
            return metaClass;
        }
        return metaMethod;
    }

    private MetaClass getMetaClassByName(String pClassname) {
        for (MetaClass metaClass : fMetaClasses) {
            if (metaClass.getName().equals(pClassname)) {
                return metaClass;
            }
        }
        return null;
    }
}
