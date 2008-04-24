package org.buildng.flexmetrics.domain.javamm;

import javax.persistence.Entity;

@Entity
public class MetaMethod extends MetaMember {
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    MetaMethod() {
    }
    
    public MetaMethod(String pName, int pLine, int pColumn) {
        super(pName, pLine, pColumn);
    }
}
