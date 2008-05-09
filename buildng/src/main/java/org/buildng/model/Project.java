package org.buildng.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        addDependency(pPath, LibraryScope.COMPILE);
        
        return this;
    }

    public Project addDependency(String pPath, LibraryScope pScope) {
        List<Library> dependencies = getLibraryDependencies(pScope);
        Library library = new Library(pPath);
        dependencies.add(library);
        
        return this;
    }

    
    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public List<Project> getProjectDependencies() {
        return fProjectDependencies;
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
    
    public void putBuilderForTaskType(TaskType pTaskType, Builder pBuilder) {
        fBuildersByTaskType.put(pTaskType, pBuilder);
    }
    


    // --------------------------------------------------------------------------
    // Object methods (overridden)
    // --------------------------------------------------------------------------

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
