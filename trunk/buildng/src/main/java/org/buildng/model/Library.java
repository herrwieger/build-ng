package org.buildng.model;

import java.io.File;

import org.apache.commons.lang.builder.ToStringBuilder;


public class Library {

    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private String fGroupId;
    private String fName;
    private String fVersion;
    private String fExtension;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public Library(String pGroupId, String pName, String pVersion, String pExtension) {
        fGroupId   = pGroupId;
        fName      = pName;
        fVersion   = pVersion;
        fExtension = pExtension;
    }



    // --------------------------------------------------------------------------
    // accessor methods
    // --------------------------------------------------------------------------

    public File getFile(File pRepositoryDir) {
        return new File(pRepositoryDir, fGroupId.replace('.', File.separatorChar) + File.separator + fName
                + File.separator + fVersion + File.separator + fName + "-" + fVersion + "." + fExtension);
    }



    // --------------------------------------------------------------------------
    // Object methods (overridden)
    // --------------------------------------------------------------------------

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
