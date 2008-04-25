package org.buildng.builders;

import java.io.File;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.JavadocTaskBuilder;
import org.buildng.elegant.type.PathTypeBuilder;
import org.buildng.flexmetrics.imports.javamm.JavaImporter;
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
    private Class<?>              fDocletClass;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public JavadocBuilder(CompilerConfiguration pCompilerConfig, String pTargetFolder, String pTestTargetFolder) {
        fConfiguration    = pCompilerConfig;
        fTargetFolder     = pTargetFolder;
        fTestTargetFolder = pTestTargetFolder;
    }



    //--------------------------------------------------------------------------  
    // fluent methods
    //--------------------------------------------------------------------------

    public JavadocBuilder doclet(Class<JavaImporter> pDocletClass) {
        fDocletClass = pDocletClass;
        
        return this;
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
            JavadocTaskBuilder javadoc = elegant.javadoc()
                .sourcepath(elegant.path().path(folder))
                .addFileset(elegant.fileSet().dir(folder).includes("*.java"))
                .classpath(classpath);
            if (fDocletClass==null) {
                javadoc.destdir(pTargetFolder);
            }
            else {
                javadoc
                    .doclet(fDocletClass.getName())
                    .docletPath(getBuildngRuntimePath());
            }
            try {
                javadoc.execute();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
    }



    private PathTypeBuilder getBuildngRuntimePath() {
        ElegantBuilder elegant = new ElegantBuilder(new File("."));
        return elegant
            .path()
            .add(elegant.path().location("bin"))
            .add(elegant.path().location("lib/org/hibernate/3.2.6.GA/antlr-2.7.6.jar"))
            .add(elegant.path().location("lib/org/hibernate/3.2.6.GA/cglib-nodep-2.1_3.jar"))
            .add(elegant.path().location("lib/org/hibernate/3.2.6.GA/commons-collections-2.1.1.jar"))
            .add(elegant.path().location("lib/org/hibernate/3.2.6.GA/commons-logging-1.0.4.jar"))
            .add(elegant.path().location("lib/org/hibernate/3.2.6.GA/dom4j-1.6.1.jar"))
            .add(elegant.path().location("lib/org/hibernate/3.2.6.GA/ehcache-1.2.3.jar"))
            .add(elegant.path().location("lib/org/hibernate/3.2.6.GA/hibernate3.jar"))
            .add(elegant.path().location("lib/org/hibernate/3.2.6.GA/jta.jar"))
            .add(elegant.path().location("lib/org/apache/ant/ant/1.7.0/ant-launcher-1.7.0.jar"))
            .add(elegant.path().location("lib/org/apache/ant/ant/1.7.0/ant-1.7.0.jar"))
            .add(elegant.path().location("lib/org/apache/commons/commons-lang/2.4/commons-lang-2.4.jar"))
            .add(elegant.path().location("lib/org/apache/log4j/log4j/1.2.15/log4j-1.2.15.jar"))
            .add(elegant.path().location("lib/org/hibernate/3.2.6.GA/hibernate3.jar"))
            .add(elegant.path().location("lib/org/hibernate/3.2.6.GA/jta.jar"))
            .add(elegant.path().location("lib/org/hibernate/annotations/3.3.1.GA/ejb3-persistence.jar"))
            .add(elegant.path().location("lib/org/hibernate/annotations/3.3.1.GA/hibernate-annotations.jar"))
            .add(elegant.path().location("lib/org/hibernate/annotations/3.3.1.GA/hibernate-commons-annotations.jar"))
            .add(elegant.path().location("lib/com/sun/jee/javaee-5.0.jar"))
            .add(elegant.path().location("lib/net/sourceforge/pmd/4.2.1/pmd-4.2.1.jar"))
            .add(elegant.path().location("lib/org/testng/testng/5.4-jdk15/testng-5.4-jdk15.jar"))
            .add(elegant.path().location("C:/Apps/Java/jdk1.6.0/db/lib/derbyclient.jar"))
            .add(elegant.path().location("lib/org/aspectj/runtime/1.6.0.20081300000005/aspectjrt.jar"));
    }
}
