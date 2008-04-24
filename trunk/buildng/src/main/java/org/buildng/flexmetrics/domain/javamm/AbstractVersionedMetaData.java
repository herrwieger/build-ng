package org.buildng.flexmetrics.domain.javamm;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.buildng.flexmetrics.domain.version.Version;
import org.buildng.flexmetrics.domain.version.Versioned;


@MappedSuperclass
public abstract class AbstractVersionedMetaData extends MetaData implements Versioned {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private Version fVersion;

    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    AbstractVersionedMetaData() {
    }
    
    public AbstractVersionedMetaData(String pName, Version pVersion) {
        super(pName);
        fVersion    = pVersion;
    }
    

    
    //--------------------------------------------------------------------------  
    // Versioned methods
    //--------------------------------------------------------------------------

    @ManyToOne()
    public Version getVersion() {
        return fVersion;
    }
    
    public void setVersion(Version pVersion) {
        fVersion    = pVersion;
    }
}
