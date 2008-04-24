package org.buildng.flexmetrics.domain.javamm;

import javax.persistence.Entity;

@Entity
public class MetaField extends MetaMember {
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    MetaField() {
    }
    
    public MetaField(String pName, int pLine, int pColumn) {
        super(pName, pLine, pColumn);
    }
}
