package org.buildng.flexmetrics.domain.javamm;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.buildng.flexmetrics.domain.project.Project;
import org.buildng.flexmetrics.domain.version.Version;


@Entity
public class SourceFile extends AbstractVersionedMetaData {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

	private Project			fProject;
    private Set<MetaClass>  fMetaClasses    = new HashSet<MetaClass>();

    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    SourceFile() {
    }
    
    public SourceFile(String pName, Project pProject, Version pVersion) {
        super(pName, pVersion);
        
        fProject = pProject;
    }
    
    
    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------
    
    @ManyToOne
	public Project getProject() {
		return fProject;
	}
    
    void setProject(Project pProject)  {
        fProject = pProject;
    }

	
    @OneToMany(cascade = CascadeType.ALL)
    Set<MetaClass> getMetaClasses() {
        return fMetaClasses;
    }

    void setMetaClasses(Set<MetaClass> pMetaClasses) {
        fMetaClasses = pMetaClasses;
    }
    
    public void addToMetaClasses(MetaClass pMetaClass) {
        fMetaClasses.add(pMetaClass);
    }

    
    
    //--------------------------------------------------------------------------  
    // SourceFile methods
    //--------------------------------------------------------------------------

    public MetaData getBestMatch(String pClassname, String pMethodname, int pLine) {
        MetaClass metaClass = getMetaClassByName(pClassname);
        if (metaClass==null) {
            return this;
        }
        MetaMethod  metaMethod = metaClass.getBestMatch(pMethodname, pLine);
        if (metaMethod==null) {
            return metaClass;
        }
        return metaMethod;
    }

    private MetaClass getMetaClassByName(String pClassname) {
        for (MetaClass metaClass : fMetaClasses) {
            if (metaClass.getName().equals(pClassname)) {
                return metaClass;
            }
        }
        return null;
    }
}
