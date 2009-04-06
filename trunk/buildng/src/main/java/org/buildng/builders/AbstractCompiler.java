package org.buildng.builders;



import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.type.PathTypeBuilder;
import org.buildng.model.Builder;
import org.buildng.model.LibraryScope;
import org.buildng.model.Model;
import org.buildng.model.Project;

/**
 * Compiles main and test sources.
 * @author towieger
 */
public abstract class AbstractCompiler implements Builder {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private CompilerConfiguration fConfiguration;

    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    /*
     * The CompilerConfiguration must provide at least one source folder. The
     * CompilerConfiguration may no test source folders.
     */
    public AbstractCompiler(CompilerConfiguration pConfiguration) {
        if (!pConfiguration.hasSourceFolders()) {
            throw new RuntimeException("CompilerConfiguration needs at least one source folder");
        }
        fConfiguration = pConfiguration;
    }


    
    //--------------------------------------------------------------------------  
    // build methods
    //--------------------------------------------------------------------------

    public void build(Model pModel, Project pProject) {
        ElegantBuilder elegant = new ElegantBuilder(pProject.getBaseDir());
        
        createFolders(elegant);
        buildMainSources(pModel, pProject, elegant);
        buildTestSources(pModel, pProject, elegant);
    }



    private void createFolders(ElegantBuilder elegant) {
        BuilderUtil.createFolders(elegant, fConfiguration.getSourceFolders());
        BuilderUtil.createFolders(elegant, fConfiguration.getTestSourceFolders());
        BuilderUtil.createFolders(elegant, fConfiguration.getTargetFolder(), fConfiguration.getTestTargetFolder());
    }

    private void buildMainSources(Model pModel, Project pProject, ElegantBuilder elegant) {
        PathTypeBuilder classpath = BuilderUtil.createMainCompileClasspath(elegant, pModel, pProject,
                                            fConfiguration.getTargetFolder(), LibraryScope.COMPILE,
                                            LibraryScope.PROVIDED);
        compile(pModel, pProject, elegant, fConfiguration.getSourceFolders(), fConfiguration.getTargetFolder(),
                classpath);
    }

    private void buildTestSources(Model pModel, Project pProject, ElegantBuilder pElegant) {
        if (!fConfiguration.hasTestSourceFolders()) {
            return;
        }
        PathTypeBuilder classpath = BuilderUtil.createMainCompileClasspath(pElegant, pModel, pProject,
                                         fConfiguration.getTargetFolder(), LibraryScope.COMPILE,
                                         LibraryScope.PROVIDED, LibraryScope.TEST);
        PathTypeBuilder projectClassesPath = pElegant.path(fConfiguration.getTargetFolder());
        classpath.add(projectClassesPath);
        compile(pModel, pProject, pElegant, fConfiguration.getTestSourceFolders(),
                fConfiguration.getTestTargetFolder(), classpath);
    }

    protected abstract void compile(Model pModel, Project pProject, ElegantBuilder pElegant, String[] pSourceFolders, String pTargetFolder, PathTypeBuilder pClasspath);
}
