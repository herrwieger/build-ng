package org.buildng.builders;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.type.BatchTestTypeBuilder;
import org.buildng.elegant.type.PathTypeBuilder;
import org.buildng.model.Builder;
import org.buildng.model.LibraryScope;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class JunitTester implements Builder {

    // --------------------------------------------------------------------------
    // instance fields
    // --------------------------------------------------------------------------

    private CompilerConfiguration fConfiguration;
    private String                fTargetFolder;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public JunitTester(CompilerConfiguration pCompilerConfig, String pTargetFolder) {
        fConfiguration = pCompilerConfig;
        fTargetFolder  = pTargetFolder;
    }



    // --------------------------------------------------------------------------
    // Builder methods
    // --------------------------------------------------------------------------

    public void build(Model pModel, Project pProject) {
        ElegantBuilder elegant = new ElegantBuilder(pProject.getBaseDir());

        BuilderUtil.createFolders(elegant, fTargetFolder);

        PathTypeBuilder classpath = BuilderUtil.createTestClasspath(elegant, pModel, pProject,
                                            fConfiguration.getTargetFolder(), fConfiguration.getTestTargetFolder(),
                                            LibraryScope.COMPILE, LibraryScope.RUNTIME, LibraryScope.PROVIDED, LibraryScope.TEST);
        BuilderUtil.addTransitiveLibraryDependencies(elegant, classpath, pModel, pProject.getProjectDependencies(),
                LibraryScope.COMPILE, LibraryScope.RUNTIME, LibraryScope.PROVIDED, LibraryScope.TEST);

        for (String sourceFolder : fConfiguration.getTestSourceFolders()) {
            elegant.junit()
                .addClasspath(classpath)
                .addClasspath(elegant.path().location(fConfiguration.getTargetFolder()))
                .addClasspath(elegant.path().location(fConfiguration.getTestTargetFolder()))
                .addFormatter(elegant.formatterElement().type(elegant.formatterElementTypeAttribute().value("xml")))
                .printsummary(elegant.jUnitTaskSummaryAttribute().value("true"))
                .addBatchTest(batchTest(elegant, sourceFolder))
                .execute();
        }
    }

    private BatchTestTypeBuilder batchTest(ElegantBuilder elegant, String sourceFolder) {
        return elegant.batchTest()
                .haltonfailure(true)
                .haltonerror(true)
                .fork(true)
                .todir(fTargetFolder)
                .addFileSet(elegant.fileSet().dir(sourceFolder).includes("**/*Test.java"));
    }
}
