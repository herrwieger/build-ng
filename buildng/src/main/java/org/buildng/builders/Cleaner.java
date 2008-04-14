package org.buildng.builders;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.model.Builder;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class Cleaner implements Builder {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private String fDirectoryToClean;
    

    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    public Cleaner(String pDirectoryToClean) {
        fDirectoryToClean = pDirectoryToClean;
    }

    
    
    //--------------------------------------------------------------------------  
    // Builder methods
    //--------------------------------------------------------------------------

    public void build(Model pModel, Project pProject) {
        ElegantBuilder elegant = new ElegantBuilder(pProject.getBaseDir());
        elegant.delete()
            .dir(fDirectoryToClean)
            .execute();
    }
}
