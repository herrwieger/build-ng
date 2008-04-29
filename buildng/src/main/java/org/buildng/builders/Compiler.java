package org.buildng.builders;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.type.PathTypeBuilder;
import org.buildng.model.Model;
import org.buildng.model.Project;



public class Compiler extends AbstractCompiler {

    // --------------------------------------------------------------------------
    // instance fields
    // --------------------------------------------------------------------------

    public Compiler(CompilerConfiguration pConfiguration) {
        super(pConfiguration);
    }


    //--------------------------------------------------------------------------  
    // AbstractCompiler methods (implementation)
    //--------------------------------------------------------------------------

    protected void compile(Model pModel, Project pProject, ElegantBuilder pElegant, String[] pSourceFolders, String pTargetFolder, PathTypeBuilder pClasspath) {
        pElegant.javac()
            .addSrc(pElegant.path(pSourceFolders))
            .classpath(pClasspath)
            .destdir(pTargetFolder)
            .includeantruntime(false)
            .execute();
    }
}
