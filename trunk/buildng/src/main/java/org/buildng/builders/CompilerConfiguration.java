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
    // fluent builder methods
    // --------------------------------------------------------------------------

    public CompilerConfiguration sourceFolders(String... pSourceFolders) {
        fSourceFolders = pSourceFolders;
        
        return this;
    }

    public CompilerConfiguration testSourceFolders(String... pTestSourceFolders) {
        fTestSourceFolders = pTestSourceFolders;
        
        return this;
    }

    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public String[] getSourceFolders() {
        return fSourceFolders;
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
