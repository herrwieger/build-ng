package org.buildng.elegant;


import java.io.File;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.buildng.elegant.type.PathTypeBuilder;


public abstract class ElegantBuilderBase {
    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private Project fProject;

    

    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public ElegantBuilderBase(File pBaseDir) {
        fProject    = new Project();
        fProject.setBaseDir(pBaseDir);
        fProject.init();
        DefaultLogger defaultLogger = new DefaultLogger();
        defaultLogger.setOutputPrintStream(System.out);
        defaultLogger.setErrorPrintStream(System.err);
        defaultLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
        fProject.addBuildListener(defaultLogger);
    }

    
    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public Project getProject() {
        return fProject;
    }


    
    //--------------------------------------------------------------------------  
    // File & Path util methods
    //--------------------------------------------------------------------------

    public FileSet createFileSet() {
        FileSet fileSet = new FileSet();
        fileSet.setProject(fProject);
        return fileSet;
    }
        
    public File createProjectRelativeFile(String pDir) {
        return new File(fProject.getBaseDir(), pDir);
    }

    public Path createPath() {
        return new Path(fProject);
    }


    public PathTypeBuilder path(String ... pFolders) {
        PathTypeBuilder path = path();
        for (String folder : pFolders) {
            path.add(path().path(folder));
        }
        return path;
    }

    public abstract PathTypeBuilder path();
}
