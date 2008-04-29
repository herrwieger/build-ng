package org.buildng.builders;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.type.PathTypeBuilder;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class AspectJCompiler extends AbstractCompiler {
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    public AspectJCompiler(CompilerConfiguration pConfiguration) {
        super(pConfiguration);
    }


    //--------------------------------------------------------------------------  
    // AbstractCompiler methods (implementation)
    //--------------------------------------------------------------------------

    @Override
    protected void compile(Model pModel, Project pProject, ElegantBuilder pElegant, String[] pSourceFolders,
            String pTargetFolder, PathTypeBuilder pClasspath) {
        
        pElegant.taskdef()
            .resource("org/aspectj/tools/ant/taskdefs/aspectjTaskdefs.properties")
            .execute();
        
        pElegant.iajc()
        .source("1.5")
        .sourceRoots(pElegant.path(pSourceFolders))
        .classpath(pClasspath)
        .destdir(pTargetFolder)
        .execute();
    }
}
