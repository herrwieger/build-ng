package org.buildng.builders;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.buildng.model.Configuration;


public class CompilerConfiguration implements Configuration {

    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private String   fTargetFolder;
    private String[] fSourceFolders     = new String[0];
    private String[] fTestSourceFolders = new String[0];
    private String   fTestTargetFolder  = "";



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public CompilerConfiguration() {
        this("target/classes", "target/test-classes");
    }

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

    public boolean hasSourceFolders() {
        return fSourceFolders.length > 0;
    }

    
    public String[] getTestSourceFolders() {
        return fTestSourceFolders;
    }

    public boolean hasTestSourceFolders() {
        return fTestSourceFolders.length > 0;
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
