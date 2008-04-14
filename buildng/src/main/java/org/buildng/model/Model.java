package org.buildng.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;


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
    
    private Map<Phase, Builder>     fDefaultBuildersByPhase = new HashMap<Phase, Builder>();



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public Model(String pBasedir) {
        fBaseDir = new File(pBasedir);
    }



    // --------------------------------------------------------------------------
    // fluent builder methods
    // --------------------------------------------------------------------------

    public Model repositoryDir(String pRepositoryDir) {
        fRepositoryDir = new File(pRepositoryDir);
        
        return this;
    }
    
    public Model putDefaultBuilderForPhase(Builder pBuilder, Phase pPhase) {
        fDefaultBuildersByPhase.put(pPhase, pBuilder);
        
        return this;
    }

    public Project createProject(String pName) {
        Project project = new Project(pName, new File(fBaseDir, pName), fDefaultBuildersByPhase);
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

    public void build(Phase pPhase) {
        Set<Project> visitedProjects = new HashSet<Project>();
        buildProjects(pPhase, visitedProjects, fProjects);
    }

    private void buildProjects(Phase pPhase, Set<Project> pVisitedProjects, List<Project> pProjects) {
        for (Project project : pProjects) {
            if (pVisitedProjects.contains(project)) {
                continue;
            }
            pVisitedProjects.add(project);
            buildProjects(pPhase, pVisitedProjects, project.getProjectDependencies());
            
            buildProject(pPhase, project);
        }
    }
    
    private void buildProject(Phase pPhase, Project project) {
        LOG.info(HEADER);
        LOG.info("Building project >>" + project.getName() + "<<");
        LOG.info(HEADER);
        LOG.info("");
        project.getBuilderForPhase(pPhase).build(this, project);
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
