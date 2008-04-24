package org.buildng.flexmetrics.domain.version;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;



@MappedSuperclass
public class AbstractVersionedData {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    protected Version fVersion;

    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    protected AbstractVersionedData() {
        // intentionally left blank
    }
    
    public AbstractVersionedData(Version pVersion) {
        fVersion = pVersion;
    }


    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    @ManyToOne
    public Version getVersion() {
        return fVersion;
    }

    public void setVersion(Version pVersion) {
        fVersion = pVersion;
    }
}
