package org.buildng.builders;

import java.io.File;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.JavadocTaskBuilder;
import org.buildng.elegant.type.JavadocDocletInfoTypeBuilder;
import org.buildng.elegant.type.PathTypeBuilder;
import org.buildng.flexmetrics.imports.javamm.JavaImporter;
import org.buildng.model.Builder;
import org.buildng.model.LibraryScope;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class JavadocBuilder implements Builder {

    // --------------------------------------------------------------------------
    // constants
    // --------------------------------------------------------------------------

    public static final String    PROJECT_OPTION = "-project";



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



    // --------------------------------------------------------------------------
    // fluent methods
    // --------------------------------------------------------------------------

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
        javadoc(elegant, pProject.getName(), classpath, fConfiguration.getSourceFolders(), fTargetFolder);

        classpath = BuilderUtil.createTestClasspath(elegant, pModel, pProject, fConfiguration.getTargetFolder(),
                            fConfiguration.getTestTargetFolder(), LibraryScope.COMPILE, LibraryScope.PROVIDED);
        javadoc(elegant, pProject.getName(), classpath, fConfiguration.getTestSourceFolders(), fTestTargetFolder);
    }

    private void javadoc(ElegantBuilder elegant, String pProjectName, PathTypeBuilder classpath,
            String[] pSourceFolders, String pTargetFolder) {
        for (String folder : pSourceFolders) {
            JavadocTaskBuilder javadoc = elegant.javadoc().sourcepath(elegant.path().path(folder)).addFileset(
                                                 elegant.fileSet().dir(folder).includes("*.java")).classpath(classpath);
            if (fDocletClass == null) {
                javadoc.destdir(pTargetFolder);
            } else {
                JavadocDocletInfoTypeBuilder docletInfo = 
                    elegant.javadocDocletInfo()
                        .name(fDocletClass.getName())
                        .path(getBuildngRuntimePath()).addParam(elegant.javadocDocletParam()
                                .name(PROJECT_OPTION)
                                .value(pProjectName));
                javadoc.addDoclet(docletInfo);
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
        return elegant.path().add(elegant.path().location("bin")).add(
                elegant.path().location("lib/org/apache/ant/ant/1.7.0/ant-launcher-1.7.0.jar")).add(
                elegant.path().location("lib/org/apache/ant/ant/1.7.0/ant-1.7.0.jar")).add(
                elegant.path().location("lib/org/apache/commons/commons-lang/2.4/commons-lang-2.4.jar")).add(
                elegant.path().location("lib/org/apache/commons/commons-logging/1.0.4/commons-logging-1.0.4.jar")).add(
                elegant.path().location("lib/org/apache/commons/commons-collections/2.1.1/commons-collections-2.1.1.jar")).add(
                elegant.path().location("lib/org/apache/log4j/log4j/1.2.15/log4j-1.2.15.jar")).add(
                elegant.path().location("lib/org/hibernate/hibernate/3.2.6.GA/antlr-2.7.6.jar")).add(
                elegant.path().location("lib/org/hibernate/hibernate/3.2.6.GA/cglib-nodep-2.1_3.jar")).add(
                elegant.path().location("lib/org/hibernate/hibernate/3.2.6.GA/dom4j-1.6.1.jar")).add(
                elegant.path().location("lib/org/hibernate/hibernate/3.2.6.GA/ehcache-1.2.3.jar")).add(
                elegant.path().location("lib/org/hibernate/hibernate/3.2.6.GA/hibernate-3.2.6.GA.jar")).add(
                elegant.path().location("lib/org/hibernate/hibernate-annotations/3.3.1.GA/hibernate-annotations-3.3.1.GA.jar")).add(
                elegant.path().location("lib/org/hibernate/hibernate-annotations/3.3.1.GA/hibernate-commons-annotations-3.3.1.GA.jar")).add(
                elegant.path().location("lib/com/sun/ejb-persistence/3.0/ejb-persistence-3.0.jar")).add(
                elegant.path().location("lib/com/sun/jee/5.0/jee-5.0.jar")).add(
                elegant.path().location("lib/com/sun/jta/1.0.1/jta-1.0.1.jar")).add(
                elegant.path().location("lib/net/sourceforge/pmd/4.2.1/pmd-4.2.1.jar")).add(
                elegant.path().location("lib/org/testng/testng/5.4-jdk15/testng-5.4-jdk15.jar")).add(
                elegant.path().location("C:/Apps/Java/jdk1.6.0/db/lib/derbyclient.jar")).add(
                elegant.path().location("lib/org/aspectj/aspectjrt/1.6.0/aspectjrt-1.6.0.jar"));
    }
}
