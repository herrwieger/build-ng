package org.buildng.flexmetrics.imports.util;


public class ImportUtil {
    //--------------------------------------------------------------------------  
    // class methods
    //--------------------------------------------------------------------------

    public static String getNormalizedFilename(String pFileName, String pPrefix) {
        String  fileName    = pFileName;
        int startPos    = pFileName.indexOf(pPrefix);
        if (startPos >= 0 ) {
            fileName = pFileName.substring(startPos + pPrefix.length());
        }
        fileName = fileName.replace('\\', '/');
        return fileName;
    }

}
