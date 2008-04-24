package org.buildng.flexmetrics.domain.javamm;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.buildng.flexmetrics.domain.version.Version;


@Entity
public class MetaPackage extends AbstractVersionedMetaData {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private Set<MetaClass>  fMetaClasses    = new HashSet<MetaClass>();



    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    MetaPackage() {
    }
    
    public MetaPackage(String pName, Version pVersion) {
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
    
    public void addToMetaClasses(MetaClass pClass) {
        fMetaClasses.add(pClass);
    }
}
