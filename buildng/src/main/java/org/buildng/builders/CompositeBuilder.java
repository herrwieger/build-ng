package org.buildng.builders;

import org.buildng.model.Builder;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class CompositeBuilder implements Builder {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private Builder[]   fBuilders;
    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    public CompositeBuilder(Builder ... pBuilders) {
        fBuilders = pBuilders;
    }

    
    
    //--------------------------------------------------------------------------  
    // Builder methods (implementation)
    //--------------------------------------------------------------------------

    public void build(Model pModel, Project pProject) {
        for (Builder builder : fBuilders) {
            builder.build(pModel, pProject);
        }
    }
}
