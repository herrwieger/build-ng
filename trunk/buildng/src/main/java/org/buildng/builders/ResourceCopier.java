package org.buildng.builders;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.model.Builder;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class ResourceCopier implements Builder {

    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private CompilerConfiguration fCompilerConfiguration;
    private String[]              fSourceFolders;
    private String[]              fTestSourceFolders;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public ResourceCopier(CompilerConfiguration pCompilerConfig) {
        fCompilerConfiguration = pCompilerConfig;
    }



    // --------------------------------------------------------------------------
    // accessor methods
    // --------------------------------------------------------------------------

    public void setSourceFolders(String... pSourceFolders) {
        fSourceFolders = pSourceFolders;
    }

    public void setTestSourceFolders(String... pTestSourceFolders) {
        fTestSourceFolders = pTestSourceFolders;
    }



    // --------------------------------------------------------------------------
    // Builder methods
    // --------------------------------------------------------------------------

    public void build(Model pModel, Project pProject) {
        ElegantBuilder elegant = new ElegantBuilder(pProject.getBaseDir());
        BuilderUtil.createFolders(elegant, fCompilerConfiguration.getTargetFolder(), fCompilerConfiguration.getTestTargetFolder());
        
        BuilderUtil.createFolders(elegant, fSourceFolders);
        BuilderUtil.createFolders(elegant, fTestSourceFolders);

        copyFoldersToTarget(elegant, fSourceFolders, fCompilerConfiguration.getTargetFolder());
        copyFoldersToTarget(elegant, fTestSourceFolders, fCompilerConfiguration.getTestTargetFolder());
    }



    private void copyFoldersToTarget(ElegantBuilder elegant, String[] pFolders, String pTargetFolder) {
        for (String folder : pFolders) {
            elegant.copy()
                .addFileset(elegant.fileSet().dir(folder))
                .todir(pTargetFolder)
                .execute();
        }
    }
}
