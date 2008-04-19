package org.buildng.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.buildng.builders.Cleaner;
import org.buildng.builders.Compiler;
import org.buildng.builders.CompilerConfiguration;
import org.buildng.builders.CompositeBuilder;
import org.buildng.builders.JarPackager;
import org.buildng.builders.JavadocBuilder;
import org.buildng.builders.JunitReporter;
import org.buildng.builders.JunitTester;
import org.buildng.builders.ResourceCopier;


public class Model {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------
    
    private static final String HEADER = "----------------------------------------";

    
    
    //--------------------------------------------------------------------------  
    // class variables
    //--------------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(Model.class);

    
    
    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private File                                  fBaseDir;
    private File                                  fRepositoryDir;
    private List<Project>                         fProjects               = new ArrayList<Project>();
    
    private Map<TaskType, Builder>     fDefaultBuildersByTaskType = new HashMap<TaskType, Builder>();



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public Model(String pBasedir) {
        fBaseDir = new File(pBasedir);
    }

    
    //--------------------------------------------------------------------------  
    // 
    //--------------------------------------------------------------------------

    public static Model createStandardModel(String pBaseDir, String pRepositoryDir) {
        Model model = new Model(pBaseDir).repositoryDir(pRepositoryDir);
        
        CompilerConfiguration compilerConfig =
            new CompilerConfiguration("target/classes", "target/test-classes")
                    .sourceFolders("src/main/java")
                    .testSourceFolders("src/test/java");
        model.putDefaultBuilderForTaskType(new Cleaner("target"), TaskType.CLEAN);
        
        ResourceCopier copier =
            new ResourceCopier(compilerConfig)
                .sourceFolders("src/main/resources")
                .testSourceFolders("src/test/resources");
        Compiler compiler = new Compiler(compilerConfig);
        model.putDefaultBuilderForTaskType(new CompositeBuilder(copier, compiler), TaskType.COMPILE);
        
        model.putDefaultBuilderForTaskType(new JunitTester(compilerConfig, "target/test"), TaskType.TEST);
        
        model.putDefaultBuilderForTaskType(new JarPackager(compilerConfig, "target"), TaskType.PACKAGE);
        
        JavadocBuilder javadocBuilder = new JavadocBuilder(compilerConfig, "target/site/javadoc",
                                                "target/site/testjavadoc");
        JunitReporter  junitReporter  = new JunitReporter("target/test", "target/site/test");
        model.putDefaultBuilderForTaskType(new CompositeBuilder(javadocBuilder, junitReporter), TaskType.SITE);
        
        return model;
    }


    // --------------------------------------------------------------------------
    // fluent builder methods
    // --------------------------------------------------------------------------

    public Model repositoryDir(String pRepositoryDir) {
        fRepositoryDir = new File(pRepositoryDir);
        
        try {
            LOG.debug("repositoryDir path=" + fRepositoryDir.getCanonicalPath());
        } catch (IOException ex) {
            // intentionally left blank. this is a debug statement. if it fails, we don't care!
        }
        
        return this;
    }
    
    public Model putDefaultBuilderForTaskType(Builder pBuilder, TaskType pTaskType) {
        fDefaultBuildersByTaskType.put(pTaskType, pBuilder);
        
        return this;
    }

    public Project createProject(String pName) {
        Project project = new Project(pName, new File(fBaseDir, pName), fDefaultBuildersByTaskType);
        fProjects.add(project);
        return project;
    }


    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public File getRepositoryDir() {
        return fRepositoryDir;
    }
    


    // --------------------------------------------------------------------------
    // build methods
    // --------------------------------------------------------------------------

    public void build(TaskType ... pTaskTypes) {
        
        for (TaskType taskType : pTaskTypes) {
            buildProjects(taskType);
        }
    }


    private void buildProjects(TaskType taskType) {
        Set<Project> visitedProjects = new HashSet<Project>();
        buildProjects(taskType, visitedProjects, fProjects);
    }

    private void buildProjects(TaskType pTaskType, Set<Project> pVisitedProjects, List<Project> pProjects) {
        for (Project project : pProjects) {
            if (pVisitedProjects.contains(project)) {
                continue;
            }
            pVisitedProjects.add(project);
            buildProjects(pTaskType, pVisitedProjects, project.getProjectDependencies());
            
            buildProject(pTaskType, project);
        }
    }
    
    private void buildProject(TaskType pTaskType, Project project) {
        LOG.info(HEADER);
        LOG.info("Building project >>" + project.getName() + "<<");
        LOG.info(HEADER);
        LOG.info("");
        project.getBuilderForTaskType(pTaskType).build(this, project);
        LOG.info(HEADER);
        LOG.info("");
        LOG.info("");
    }



    // --------------------------------------------------------------------------
    // Object methods (overridden)
    // --------------------------------------------------------------------------

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
