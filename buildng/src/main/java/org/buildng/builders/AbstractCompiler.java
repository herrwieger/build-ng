package org.buildng.builders;



import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.type.PathTypeBuilder;
import org.buildng.model.Builder;
import org.buildng.model.LibraryScope;
import org.buildng.model.Model;
import org.buildng.model.Project;

public abstract class AbstractCompiler implements Builder {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private CompilerConfiguration fConfiguration;

    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    public AbstractCompiler(CompilerConfiguration pConfiguration) {
        fConfiguration = pConfiguration;
    }


    
    //--------------------------------------------------------------------------  
    // build methods
    //--------------------------------------------------------------------------

    public void build(Model pModel, Project pProject) {
        ElegantBuilder elegant = new ElegantBuilder(pProject.getBaseDir());
        
        BuilderUtil.createFolders(elegant, fConfiguration.getSourceFolders());
        BuilderUtil.createFolders(elegant, fConfiguration.getTestSourceFolders());
        BuilderUtil.createFolders(elegant, fConfiguration.getTargetFolder(), fConfiguration.getTestTargetFolder());
    
        PathTypeBuilder classpath = BuilderUtil.createMainCompileClasspath(elegant, pModel, pProject,
                                            fConfiguration.getTargetFolder(), LibraryScope.COMPILE,
                                            LibraryScope.PROVIDED);
        compile(pModel, pProject, elegant, fConfiguration.getSourceFolders(), fConfiguration.getTargetFolder(),
                classpath);
    
                        classpath          = BuilderUtil.createMainCompileClasspath(elegant, pModel, pProject,
                                                     fConfiguration.getTargetFolder(), LibraryScope.COMPILE,
                                                     LibraryScope.PROVIDED, LibraryScope.TEST);
        PathTypeBuilder projectClassesPath = elegant.path(fConfiguration.getTargetFolder());
        classpath.add(projectClassesPath);
        compile(pModel, pProject, elegant, fConfiguration.getTestSourceFolders(), fConfiguration.getTestTargetFolder(),
                classpath);
    }

    
    protected abstract void compile(Model pModel, Project pProject, ElegantBuilder pElegant, String[] pSourceFolders, String pTargetFolder, PathTypeBuilder pClasspath);
}
