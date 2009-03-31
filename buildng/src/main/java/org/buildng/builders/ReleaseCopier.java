package org.buildng.builders;

import java.io.File;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.model.Builder;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class ReleaseCopier implements Builder {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private String fPackageTargetFolder;
    private File fTargetDir;

    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    public ReleaseCopier(String pPackageTargetFolder, File pTargetDir) {
        fPackageTargetFolder = pPackageTargetFolder;
        fTargetDir = pTargetDir;
    }

    public void build(Model pModel, Project pProject) {
        ElegantBuilder elegant = new ElegantBuilder(pProject.getBaseDir());

        elegant.copy()
            .file(fPackageTargetFolder + File.separator + pProject.getName() + ".jar")
            .todir(fTargetDir)
            .execute();
    }
}
