package org.buildng.builders;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.buildng.model.Configuration;


public class CompilerConfiguration implements Configuration {

    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private String   fTargetFolder;
    private String[] fSourceFolders;
    private String[] fTestSourceFolders;
    private String   fTestTargetFolder;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public CompilerConfiguration(String pTargetFolder, String pTestTargetFolder) {
        fTargetFolder     = pTargetFolder;
        fTestTargetFolder = pTestTargetFolder;
    }



    // --------------------------------------------------------------------------
    // instance methods
    // --------------------------------------------------------------------------

    public void setSourceFolders(String... pSourceFolders) {
        fSourceFolders = pSourceFolders;
    }

    public String[] getSourceFolders() {
        return fSourceFolders;
    }

    public void setTestSourceFolders(String... pTestSourceFolders) {
        fTestSourceFolders = pTestSourceFolders;
    }

    public String[] getTestSourceFolders() {
        return fTestSourceFolders;
    }

    public String getTargetFolder() {
        return fTargetFolder;
    }

    public String getTestTargetFolder() {
        return fTestTargetFolder;
    }



    // --------------------------------------------------------------------------
    // Object methods (overridden)
    // --------------------------------------------------------------------------

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
