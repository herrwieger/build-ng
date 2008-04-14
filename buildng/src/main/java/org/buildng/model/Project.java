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

    private String              fName;
    private File                fBaseDir;
    private Map<Phase, Builder> fBuildersByPhase;

    private List<Project>                    fProjectDependencies        = new ArrayList<Project>();
    private Map<LibraryScope, List<Library>> fLibraryDependenciesByScope = new HashMap<LibraryScope, List<Library>>();



    // --------------------------------------------------------------------------
    // constructors & factory methods
    // --------------------------------------------------------------------------

    public Project(String pName, File pBasedir, Map<Phase, Builder> pBuildersByPhase) {
        fName    = pName;
        fBaseDir = pBasedir;

        fBuildersByPhase = new HashMap<Phase, Builder>(pBuildersByPhase);
    }



    // --------------------------------------------------------------------------
    // instance methods
    // --------------------------------------------------------------------------

    public void addDependency(Project pProject) {
        fProjectDependencies.add(pProject);
    }

    public void addDependency(String pGroupId, String pName, String pVersion) {
        addDependency(pGroupId, pName, pVersion, LibraryScope.COMPILE);
    }

    public void addDependency(String pGroupId, String pName, String pVersion, String pExtension) {
        addDependency(pGroupId, pName, pVersion, pExtension, LibraryScope.COMPILE);
    }

    public void addDependency(String pGroupId, String pName, String pVersion, LibraryScope pScope) {
        addDependency(pGroupId, pName, pVersion, "jar", pScope);
    }

    public void addDependency(String pGroupId, String pName, String pVersion, String pExtension, LibraryScope pScope) {
        List<Library> dependencies = getLibraryDependencies(pScope);
        dependencies.add(new Library(pGroupId, pName, pVersion, pExtension));
    }

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

    public Builder getBuilderForPhase(Phase pPhase) {
        return fBuildersByPhase.get(pPhase);
    }
    


    // --------------------------------------------------------------------------
    // Object methods (overridden)
    // --------------------------------------------------------------------------

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
