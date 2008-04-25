package org.buildng.flexmetrics.domain.version;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "XVERSION")
public class Version {

    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private int     fId;
    private String  fLabel;
    /**
     * Marker for the current Version.
     */
    private boolean fIsCurrent;



    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    Version() {
    }

    /**
     * creates a new version with isCurrent set to true.
     * 
     * @param pLabel
     */
    public Version(String pLabel) {
        fLabel     = pLabel;
        fIsCurrent = true;
    }



    // --------------------------------------------------------------------------
    // accessor methods
    // --------------------------------------------------------------------------

    @Id
    @GeneratedValue
    int getId() {
        return fId;
    }

    void setId(int pId) {
        fId = pId;
    }

    public String getLabel() {
        return fLabel;
    }

    void setLabel(String pLabel) {
        fLabel = pLabel;
    }


    @Column(name = "CURRENT_FLAG")
    public boolean isCurrent() {
        return fIsCurrent;
    }

    public void setCurrent(boolean pIsCurrent) {
        fIsCurrent = pIsCurrent;
    }
}
