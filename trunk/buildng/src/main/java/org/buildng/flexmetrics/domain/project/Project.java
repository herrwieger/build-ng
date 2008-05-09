package org.buildng.flexmetrics.domain.project;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Project{
	//--------------------------------------------------------------------------  
	// instance variables
	//--------------------------------------------------------------------------

    private int    fId;
	private String fName;


	
	//--------------------------------------------------------------------------  
	// constructors
	//--------------------------------------------------------------------------

	Project() {		
	}
	
	public Project(String pName) {
		fName = pName;
	}

	

	//--------------------------------------------------------------------------  
	// accessor methods
	//--------------------------------------------------------------------------

    @Id
    @GeneratedValue
    int getId() {
        return fId;
    }
    
    void setId(int pId) {
        fId = pId;
    }

    
	public String getName() {
		return fName;
	}
	
	void setName(String pName) {
	    fName = pName;
	}
}
