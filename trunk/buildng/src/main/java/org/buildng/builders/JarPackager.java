package org.buildng.builders;

import java.io.File;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.model.Builder;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class JarPackager implements Builder {

    // --------------------------------------------------------------------------
    // instance fields
    // --------------------------------------------------------------------------

    private CompilerConfiguration fConfiguration;
    private String                fPackageTargetFolder;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public JarPackager(CompilerConfiguration pConfiguration, String pPackageTargetFolder) {
        fConfiguration       = pConfiguration;
        fPackageTargetFolder = pPackageTargetFolder;
    }



    // --------------------------------------------------------------------------
    // Builder methods (implementation)
    // --------------------------------------------------------------------------

    public void build(Model pModel, Project pProject) {
        ElegantBuilder elegant = new ElegantBuilder(pProject.getBaseDir());

        elegant.jar()
            .basedir(fConfiguration.getTargetFolder())
            .destFile(fPackageTargetFolder + File.separator + pProject.getName() + ".jar")
            .execute();
    }
}
