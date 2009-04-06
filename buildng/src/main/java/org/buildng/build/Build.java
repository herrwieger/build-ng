package org.buildng.build;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.buildng.builders.BuilderUtil;
import org.buildng.builders.Compiler;
import org.buildng.builders.CompilerConfiguration;
import org.buildng.defaults.Defaults;
import org.buildng.elegant.ElegantBuilder;
import org.buildng.model.LibraryScope;
import org.buildng.model.Model;
import org.buildng.model.Project;
import org.buildng.model.TaskType;


public class Build {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------

    private static final String LIB_DIR = "lib";

    
    //--------------------------------------------------------------------------  
    // class methods
    //--------------------------------------------------------------------------

    /**
     * @param pArgs
     */
    public static void main(String[] pArgs) {
        Model model = Defaults.createDefaultModel(".", LIB_DIR);
        Project buildProject = setupBuildProject(model);
        CompilerConfiguration config = new CompilerConfiguration()
            .sourceFolders("src/buildng/java");
        setupCompiler(buildProject, config);        
        model.build(TaskType.CLEAN, TaskType.COMPILE);
        runBuild(model, buildProject, config);
    }
    
    private static Project setupBuildProject(Model model) {
        Project buildProject = model.createProject("build", new File("."));
        File libDir = new File(LIB_DIR);
        addDependencies(buildProject, libDir, safeCanonicalPath(libDir));
        buildProject.addExternalDependency(System.getenv("JAVA_HOME") + "/lib/tools.jar", LibraryScope.RUNTIME);
        return buildProject;
    }

    private static final FileFilter JAR_FILE_FILTER = new FileFilter(){
        public boolean accept(File pFile) {
            return pFile.getName().endsWith(".jar") || pFile.isDirectory();
        }
    };
    
    private static void addDependencies(Project buildProject, File pDirectory, String pRootPath) {
        for (File libFile : pDirectory.listFiles(JAR_FILE_FILTER)) {
            if (libFile.isDirectory()) {
                addDependencies(buildProject, libFile, pRootPath);
            } else {
                buildProject.addDependency(safeCanonicalPath(libFile).substring(pRootPath.length()));
            }
        }
    }

    private static String safeCanonicalPath(File libDir) {
        try {
        return libDir.getCanonicalPath();
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    
    private static CompilerConfiguration setupCompiler(Project buildProject, CompilerConfiguration pConfig) {
        Compiler compiler = new Compiler(pConfig);
        buildProject.putBuilderForTaskType(compiler, TaskType.COMPILE);
        return pConfig;
    }

    private static void runBuild(Model model, Project buildProject, CompilerConfiguration config) {
        System.out.println(System.getenv("JAVA_HOME"));
        ElegantBuilder elegant = new ElegantBuilder(buildProject.getBaseDir());
        elegant.java()
            .classpath(
                BuilderUtil.createTestRuntimeClasspath(elegant, model, buildProject,
                        config.getTargetFolder(), config.getTestTargetFolder(),
                        LibraryScope.COMPILE, LibraryScope.RUNTIME))
           .classname("Build")
           .execute();
        
    }
}
