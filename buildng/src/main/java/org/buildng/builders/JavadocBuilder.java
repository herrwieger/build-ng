package org.buildng.builders;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.type.PathTypeBuilder;
import org.buildng.model.Builder;
import org.buildng.model.LibraryScope;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class JavadocBuilder implements Builder {

    // --------------------------------------------------------------------------
    // instance fields
    // --------------------------------------------------------------------------

    private CompilerConfiguration fConfiguration;
    private String                fTargetFolder;
    private String                fTestTargetFolder;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public JavadocBuilder(CompilerConfiguration pCompilerConfig, String pTargetFolder, String pTestTargetFolder) {
        fConfiguration    = pCompilerConfig;
        fTargetFolder     = pTargetFolder;
        fTestTargetFolder = pTestTargetFolder;
    }



    // --------------------------------------------------------------------------
    // Builder methods
    // --------------------------------------------------------------------------

    public void build(Model pModel, Project pProject) {
        ElegantBuilder elegant = new ElegantBuilder(pProject.getBaseDir());

        PathTypeBuilder classpath = BuilderUtil.createClasspath(elegant, pModel, pProject,
                                            fConfiguration.getTargetFolder(), LibraryScope.COMPILE,
                                            LibraryScope.PROVIDED);
        javadoc(elegant, classpath, fConfiguration.getSourceFolders(), fTargetFolder);

        classpath = BuilderUtil.createTestClasspath(elegant, pModel, pProject, fConfiguration.getTargetFolder(),
                            fConfiguration.getTestTargetFolder(), LibraryScope.COMPILE, LibraryScope.PROVIDED);
        javadoc(elegant, classpath, fConfiguration.getTestSourceFolders(), fTestTargetFolder);
    }

    private void javadoc(ElegantBuilder elegant, PathTypeBuilder classpath, String[] pSourceFolders,
            String pTargetFolder) {
        for (String folder : pSourceFolders) {
            elegant.javadoc()
                .sourcepath(elegant.path().path(folder))
                .addFileset(elegant.fileSet().dir(folder).includes("*.java"))
                .classpath(classpath)
                .destdir(pTargetFolder)
                .execute();
        }
    }
}
