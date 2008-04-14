package org.buildng.model;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.buildng.builders.Cleaner;
import org.buildng.builders.Compiler;
import org.buildng.builders.CompilerConfiguration;
import org.buildng.builders.CompositeBuilder;
import org.buildng.builders.JarPackager;
import org.buildng.builders.JavadocBuilder;
import org.buildng.builders.JunitReporter;
import org.buildng.builders.JunitTester;
import org.buildng.builders.ResourceCopier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@Test
public class TestModel {
    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private Model            fModel;    

    

    // --------------------------------------------------------------------------
    // setup methods
    // --------------------------------------------------------------------------

    @BeforeClass
    public void setUp() {
        fModel = createModel();

        CompilerConfiguration compilerConfig =
            new CompilerConfiguration("target/classes", "target/test-classes")
                    .sourceFolders("src/main/java")
                    .testSourceFolders("src/test/java");
        fModel.putDefaultBuilderForPhase(new Cleaner("target"), Phase.CLEAN);
        
        ResourceCopier copier =
            new ResourceCopier(compilerConfig)
                .sourceFolders("src/main/resources")
                .testSourceFolders("src/test/resources");
        Compiler compiler = new Compiler(compilerConfig);
        fModel.putDefaultBuilderForPhase(new CompositeBuilder(copier, compiler), Phase.COMPILE);
        
        fModel.putDefaultBuilderForPhase(new JunitTester(compilerConfig, "target/test"), Phase.TEST);
        
        fModel.putDefaultBuilderForPhase(new JarPackager(compilerConfig, "target"), Phase.PACKAGE);
        
        JavadocBuilder javadocBuilder = new JavadocBuilder(compilerConfig, "target/site/javadoc",
                                                "target/site/testjavadoc");
        JunitReporter  junitReporter  = new JunitReporter("target/test", "target/site/test");
        fModel.putDefaultBuilderForPhase(new CompositeBuilder(javadocBuilder, junitReporter), Phase.SITE);

        
        Project applicationProject =
            fModel.createProject("application")
                .addDependency("junit", "junit", "4.4", LibraryScope.TEST);

        Project domainProject =
            fModel.createProject("domain")
                .addDependency("junit", "junit", "3.8.1", LibraryScope.TEST)
                .addDependency("commons-lang", "commons-lang", "2.3")
                .addDependency("log4j", "log4j", "1.2.14");

        Project moneyProject =
            fModel.createProject("money")
                .addDependency("junit", "junit", "3.8.1", LibraryScope.TEST);

        applicationProject
            .addDependency(domainProject)
            .addDependency(moneyProject);
    }

    // --------------------------------------------------------------------------
    // test methods
    // --------------------------------------------------------------------------

    private Model createModel() {
        Model model = new Model("C:/Work/Home/build_systems/trunk");
        model.repositoryDir("C:/Library/Java");
        
        return model;
    }

    public void testBuildProjectOrder() {
        Model            model            = createModel();
        TestOrderBuilder testOrderBuilder = new TestOrderBuilder();
        model.putDefaultBuilderForPhase(testOrderBuilder, Phase.COMPILE);
        Project applicationProject = model.createProject("application");
        Project domainProject      = model.createProject("domain");
        Project moneyProject       = model.createProject("money");
        applicationProject.addDependency(domainProject);
        applicationProject.addDependency(moneyProject);
        
        model.build(Phase.COMPILE);
        assertEquals(testOrderBuilder.getOrderedProjects().size(), 3);
        assertEquals(testOrderBuilder.getOrderedProjects().get(0), domainProject);
        assertEquals(testOrderBuilder.getOrderedProjects().get(1), moneyProject);
        assertEquals(testOrderBuilder.getOrderedProjects().get(2), applicationProject);
    }

    public static class TestOrderBuilder implements Builder {

        List<Project> fOrderedProjects = new ArrayList<Project>();

        public void build(Model pModel, Project pProject) {
            fOrderedProjects.add(pProject);
        }

        public List<Project> getOrderedProjects() {
            return fOrderedProjects;
        }
    }

    
    public void testClean() {
        fModel.build(Phase.CLEAN);
    }
    
    @Test(dependsOnMethods = {"testClean"})
    public void testCompile() {
        fModel.build(Phase.COMPILE);
    }

    @Test(dependsOnMethods = {"testCompile"})
    public void testTest() {
        fModel.build(Phase.TEST);
    }
    
    @Test(dependsOnMethods = {"testCompile"})
    public void testPackage() {
        fModel.build(Phase.PACKAGE);
    }


    public void testJavadoc() {
        fModel.build(Phase.SITE);
    }
}
