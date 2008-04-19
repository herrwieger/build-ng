package org.buildng.model;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;


public class Library {

    // --------------------------------------------------------------------------
    // class variables
    // --------------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(Library.class);



    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private String              fGroupId;
    private String              fName;
    private String              fVersion;
    private String              fSecondaryName;
    private String              fExtension;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public Library(String pGroupId, String pName, String pVersion, String pExtension) {
        this(pGroupId, pName, pVersion, null, pExtension);
    }
    
    public Library(String pGroupId, String pName, String pVersion, String pSecondaryName, String pExtension) {
        fGroupId       = pGroupId;
        fName          = pName;
        fVersion       = pVersion;
        fSecondaryName = pSecondaryName;
        fExtension     = pExtension;
    }




    // --------------------------------------------------------------------------
    // accessor methods
    // --------------------------------------------------------------------------

    public File getFile(File pRepositoryDir) {
        String pathToArtifact = fGroupId.replace('.', File.separatorChar) + File.separator + fName
                                       + File.separator + fVersion;
        File jarFile;
        if (fSecondaryName == null) {
            jarFile = new File(pRepositoryDir, pathToArtifact + File.separator + fName + "-" + fVersion + "." + fExtension);
        } else {
            jarFile = new File(pRepositoryDir, pathToArtifact + File.separator + fSecondaryName + "-" + fVersion + "." + fExtension);
        }
        try {
            LOG.debug("jarfile path=" + jarFile.getCanonicalPath());
        } catch (IOException ex) {
            // intentionally left blank. this is a debug statement. if it fails,
            // we don't care!
        }
        return jarFile;
    }



    // --------------------------------------------------------------------------
    // Object methods (overridden)
    // --------------------------------------------------------------------------

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
