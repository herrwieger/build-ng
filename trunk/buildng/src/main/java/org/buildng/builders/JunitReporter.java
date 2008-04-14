package org.buildng.builders;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.model.Builder;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class JunitReporter implements Builder {

    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private String fSourceFolder;
    private String fTargetFolder;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public JunitReporter(String pSourceFolder, String pTargetFolder) {
        fSourceFolder = pSourceFolder;
        fTargetFolder = pTargetFolder;
    }

    
    
    //--------------------------------------------------------------------------  
    // builder methods (implementation)
    //--------------------------------------------------------------------------

    public void build(Model pModel, Project pProject) {
        ElegantBuilder elegant   = new ElegantBuilder(pProject.getBaseDir());
        
        BuilderUtil.createFolders(elegant, fSourceFolder, fTargetFolder);
        
        elegant.junitreport()
            .addFileSet(elegant.fileSet().dir(fSourceFolder).includes("TEST-*.xml"))
            .todir(fTargetFolder)
            .execute();
    }
}
