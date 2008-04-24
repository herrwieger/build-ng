package org.buildng.flexmetrics.domain.javamm;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MetaData {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private int     fId;
    private String  fName;

    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    MetaData() {
    }

    public MetaData(String pName) {
        fName   = pName;
    }

    
    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    @Id
    @GeneratedValue
    public int getId() {
        return fId;
    }

    public void setId(int pId) {
        fId = pId;
    }

    
    public String getName() {
        return fName;
    }

    public void setName(String pName) {
        fName = pName;
    }

    
    
    //--------------------------------------------------------------------------  
    // object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + fId;
        return result;
    }

    @Override
    public boolean equals(Object pOtherObject) {
        if (this == pOtherObject)
            return true;
        if (pOtherObject == null)
            return false;
        if (getClass() != pOtherObject.getClass())
            return false;
        final MetaData other = (MetaData) pOtherObject;
        if (fId != other.fId)
            return false;
        return true;
    }    
}
