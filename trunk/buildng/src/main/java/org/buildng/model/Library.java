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

    private String              fPath;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public Library(String pPath) {
        fPath = pPath;
    }




    // --------------------------------------------------------------------------
    // accessor methods
    // --------------------------------------------------------------------------

    public File getFile(File pRepositoryDir) {
        File jarFile = new File(pRepositoryDir, fPath);
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
