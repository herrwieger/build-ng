package org.buildng.model;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;


public class ExternalLibrary implements Library {

    // --------------------------------------------------------------------------
    // class variables
    // --------------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(RepositoryLibrary.class);



    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private String              fPath;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public ExternalLibrary(String pPath) {
        fPath = pPath;
    }




    // --------------------------------------------------------------------------
    // accessor methods
    // --------------------------------------------------------------------------

    public File getFile(File pRepositoryDir) {
        File jarFile = new File(fPath);
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




    @Override
    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
                  result = prime * result + ((fPath == null) ? 0 : fPath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ExternalLibrary other = (ExternalLibrary) obj;
        if (fPath == null) {
            if (other.fPath != null)
                return false;
        } else if (!fPath.equals(other.fPath))
            return false;
        return true;
    }
}
