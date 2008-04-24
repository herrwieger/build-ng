package org.buildng.flexmetrics.domain.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.buildng.flexmetrics.domain.javamm.MetaData;
import org.buildng.flexmetrics.domain.version.AbstractVersionedData;
import org.buildng.flexmetrics.domain.version.Version;


@Entity
@Table(name = "XAUDIT")
public class Audit extends AbstractVersionedData {
    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private int      fId;

    private MetaData fMetaData;

    private int      fLine;
    private int      fColumn;
    private String   fRuleName;
    private String   fPriority;
    private String   fMessage;
    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    Audit() {
        // intentionally left blank
    }
    
    public Audit(MetaData pMetaData, String pRulename, String pPriority, String pMessage, Version pVersion) {
        super(pVersion);
        fMetaData   = pMetaData;
        
        fRuleName   = pRulename;
        fPriority   = pPriority;
        fMessage    = pMessage;
    }



    // --------------------------------------------------------------------------
    // accessor methods
    // --------------------------------------------------------------------------


    @Id
    @GeneratedValue
    public int getId() {
        return fId;
    }

    public void setId(int pId) {
        fId = pId;
    }

    
    @ManyToOne
    public MetaData getMetaData() {
        return fMetaData;
    }

    public void setMetaData(MetaData pMetaData) {
        fMetaData = pMetaData;
    }

    @Column(name = "XCOLUMN" )
    public int getColumn() {
        return fColumn;
    }

    public void setColumn(int pColumn) {
        fColumn = pColumn;
    }

    public int getLine() {
        return fLine;
    }

    public void setLine(int pLine) {
        fLine = pLine;
    }

    public String getMessage() {
        return fMessage;
    }

    public void setMessage(String pMessage) {
        fMessage = pMessage;
    }

    public String getPriority() {
        return fPriority;
    }

    public void setPriority(String pPriority) {
        fPriority = pPriority;
    }

    public String getRuleName() {
        return fRuleName;
    }

    public void setRuleName(String pRuleName) {
        fRuleName = pRuleName;
    }
}
