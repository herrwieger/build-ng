package org.buildng.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;


public class Project {
    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private String                           fName;
    private File                             fBaseDir;
    private Map<TaskType, Builder>           fBuildersByTaskType;

    private List<Project>                    fProjectDependencies        = new ArrayList<Project>();
    private Map<LibraryScope, List<Library>> fLibraryDependenciesByScope = new HashMap<LibraryScope, List<Library>>();



    // --------------------------------------------------------------------------
    // constructors & factory methods
    // --------------------------------------------------------------------------

    public Project(String pName, File pBasedir, Map<TaskType, Builder> pBuildersByTaskType) {
        fName    = pName;
        fBaseDir = pBasedir;

        fBuildersByTaskType = new HashMap<TaskType, Builder>(pBuildersByTaskType);
    }



    // --------------------------------------------------------------------------
    // fluent builder methods
    // --------------------------------------------------------------------------

    public Project addDependency(Project pProject) {
        fProjectDependencies.add(pProject);
        
        return this;
    }

    
    public Project addDependency(String pPath) {
        return addDependency(pPath, LibraryScope.COMPILE);
    }

    public Project addDependency(String pPath, LibraryScope pScope) {
        List<Library> dependencies = getLibraryDependencies(pScope);
        RepositoryLibrary library = new RepositoryLibrary(pPath);
        dependencies.add(library);
        
        return this;
    }

    public Project addExternalDependency(String pPath) {
        return addExternalDependency(pPath, LibraryScope.COMPILE);
    }
    
    public Project addExternalDependency(String pPath, LibraryScope pScope) {
        List<Library> dependencies = getLibraryDependencies(pScope);
        ExternalLibrary library = new ExternalLibrary(pPath);
        dependencies.add(library);
        
        return this;
    }
    
    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public Set<Project> getTransitiveProjectDependencies() {
        Set<Project> projects = new HashSet<Project>();
        addTransitiveProjectDependencies(projects);
        return projects;
    }
    
    private void addTransitiveProjectDependencies(Set<Project> pProjects) {
        for (Project project : fProjectDependencies) {
            project.addTransitiveProjectDependencies(pProjects);
        }
        pProjects.add(this);
    }



    public List<Project> getProjectDependencies() {
        return fProjectDependencies;
    }

    public Collection<Library> getTransitiveLibraryDependencies(LibraryScope[] pScopes) {
        Set<Library> libraryDependencies = new HashSet<Library>();
        addTransitiveLibraryDependencies(pScopes, libraryDependencies);
        return libraryDependencies;
    }


    private void addTransitiveLibraryDependencies(LibraryScope[] pScopes, Set<Library> pLibraryDependencies) {
        for (Project project : fProjectDependencies) {
            project.addTransitiveLibraryDependencies(pScopes, pLibraryDependencies);
        }
        pLibraryDependencies.addAll(getLibraryDependencies(pScopes));
    }
    
    public Set<Library> getLibraryDependencies(LibraryScope ... pScopes) {
        Set<Library> libraryDependencies = new HashSet<Library>();
        for (LibraryScope scope : pScopes) {
            libraryDependencies.addAll(getLibraryDependencies(scope));
        }
        return libraryDependencies;
    }
    
    
    /**
     * return the library dependencies for scope pScope.
     * 
     * @param pScope
     * @return will allways return a List, never null.
     */
    public List<Library> getLibraryDependencies(LibraryScope pScope) {
        List<Library> dependencies = fLibraryDependenciesByScope.get(pScope);
        if (dependencies == null) {
            dependencies = new ArrayList<Library>();
            fLibraryDependenciesByScope.put(pScope, dependencies);
        }
        return dependencies;
    }

    public File getBaseDir() {
        return fBaseDir;
    }

    public String getName() {
        return fName;
    }

    public Builder getBuilderForTaskType(TaskType pTaskType) {
        return fBuildersByTaskType.get(pTaskType);
    }
    
    public void putBuilderForTaskType(Builder pBuilder, TaskType pTaskType) {
        fBuildersByTaskType.put(pTaskType, pBuilder);
    }
    


    // --------------------------------------------------------------------------
    // Object methods (overridden)
    // --------------------------------------------------------------------------

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
