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

    
    public Project addDependency(String pGroupId, String pName, String pVersion) {
        addDependency(pGroupId, pName, pVersion, LibraryScope.COMPILE);
        
        return this;
    }

    public Project addDependency(String pGroupId, String pName, String pVersion, String pExtension) {
        addDependency(pGroupId, pName, pVersion, pExtension, LibraryScope.COMPILE);
        
        return this;
    }

    public Project addDependency(String pGroupId, String pName, String pVersion, LibraryScope pScope) {
        addDependency(pGroupId, pName, pVersion, "jar", pScope);
        
        return this;
    }

    public Project addDependency(String pGroupId, String pName, String pVersion, String pExtension, LibraryScope pScope) {
        List<Library> dependencies = getLibraryDependencies(pScope);
        dependencies.add(new Library(pGroupId, pName, pVersion, pExtension));
        
        return this;
    }

    public Project addSecondaryDependency(String pGroupId, String pName, String pVersion, String pSecondaryName) {
        addSecondaryDependency(pGroupId, pName, pVersion, pSecondaryName, LibraryScope.COMPILE);
        
        return this;
    }
    
    public Project addSecondaryDependency(String pGroupId, String pName, String pVersion, String pSecondaryName, String pExtension) {
        addSecondaryDependency(pGroupId, pName, pVersion, pSecondaryName, pExtension, LibraryScope.COMPILE);
        
        return this;
    }
    
    private Project addSecondaryDependency(String pGroupId, String pName, String pVersion, String pSecondaryName,
            LibraryScope pScope) {
        addSecondaryDependency(pGroupId, pName, pVersion, pSecondaryName, "jar", pScope);        
        
        return this;
    }
    
    private Project addSecondaryDependency(String pGroupId, String pName, String pVersion, String pSecondaryName,
            String pExtension, LibraryScope pScope) {
        
        List<Library> dependencies = getLibraryDependencies(pScope);
        dependencies.add(new Library(pGroupId, pName, pVersion, pSecondaryName, pExtension));
        
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
