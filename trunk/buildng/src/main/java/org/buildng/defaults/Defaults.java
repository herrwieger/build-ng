package org.buildng.defaults;

import org.buildng.builders.Cleaner;
import org.buildng.builders.Compiler;
import org.buildng.builders.CompilerConfiguration;
import org.buildng.builders.CompositeBuilder;
import org.buildng.builders.JarPackager;
import org.buildng.builders.JavadocBuilder;
import org.buildng.builders.JunitReporter;
import org.buildng.builders.JunitTester;
import org.buildng.builders.ReleaseCopier;
import org.buildng.builders.ResourceCopier;
import org.buildng.flexmetrics.imports.javamm.JavaImporter;
import org.buildng.model.Model;
import org.buildng.model.TaskType;
import org.buildng.reporters.PMDReporter;


public class Defaults {

    public static Model createDefaultModel(String pBaseDir, String pRepositoryDir) {
        Model model = new Model(pBaseDir).repositoryDir(pRepositoryDir);
        
        CompilerConfiguration compilerConfig =
            new CompilerConfiguration("target/classes", "target/test-classes")
                    .sourceFolders("src/main/java")
                    .testSourceFolders("src/test/java");
        model.putDefaultBuilderForTaskType(new Cleaner("target"), TaskType.CLEAN);
        
        ResourceCopier copier =
            new ResourceCopier(compilerConfig)
                .sourceFolders("src/main/resources")
                .testSourceFolders("src/test/resources");
        Compiler compiler = new Compiler(compilerConfig);
        model.putDefaultBuilderForTaskType(new CompositeBuilder(copier, compiler), TaskType.COMPILE);
        
        model.putDefaultBuilderForTaskType(new JunitTester(compilerConfig, "target/test"), TaskType.TEST);
        
        model.putDefaultBuilderForTaskType(new JarPackager(compilerConfig, "target"), TaskType.PACKAGE);
        
        model.putDefaultBuilderForTaskType(new ReleaseCopier("target", model.getRepositoryDir()), TaskType.RELEASE);
        
        JavadocBuilder javadocBuilder = new JavadocBuilder(compilerConfig, "target/reports/javadoc",
                                                "target/reports/testjavadoc");
        JunitReporter  junitReporter  = new JunitReporter("target/test", "target/reports/test");
        // TODO: introduce a subclass of JavadocBuilder or strategy for the javadoc method
        JavadocBuilder jmmReporter    = new JavadocBuilder(compilerConfig, "target/reports/javadoc",
                                                "target/reports/testjavadoc");
        jmmReporter.doclet(JavaImporter.class);
        PMDReporter    pmdReporter    = new PMDReporter();
        model.putDefaultBuilderForTaskType(new CompositeBuilder(javadocBuilder, junitReporter, jmmReporter, pmdReporter), TaskType.REPORTS);
        
        return model;
    }

}
