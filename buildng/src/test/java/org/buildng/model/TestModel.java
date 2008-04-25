package org.buildng.model;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.buildng.defaults.Defaults;
import org.buildng.flexmetrics.domain.version.VersionMgr;
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

        Project applicationProject =
            fModel.createProject("application")
                .addDependency("junit", "junit", "4.4", LibraryScope.TEST);

        Project domainProject =
            fModel.createProject("domain")
                .addDependency("junit", "junit", "3.8.1", LibraryScope.TEST)
                .addDependency("commons-lang", "commons-lang", "2.3")
                .addDependency("commons-collections", "commons-collections", "3.2")
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
        return Defaults.createDefaultModel("C:/Work/Home/build_systems/trunk", "C:/Library/Java");
    }

    public void testBuildProjectOrder() {
        Model            model            = createModel();
        TestOrderBuilder testOrderBuilder = new TestOrderBuilder();
        model.putDefaultBuilderForTaskType(testOrderBuilder, TaskType.COMPILE);
        Project applicationProject = model.createProject("application");
        Project domainProject      = model.createProject("domain");
        Project moneyProject       = model.createProject("money");
        applicationProject.addDependency(domainProject);
        applicationProject.addDependency(moneyProject);
        
        model.build(TaskType.COMPILE);
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
        fModel.build(TaskType.CLEAN);
    }
    
    @Test(dependsOnMethods = {"testClean"})
    public void testCompile() {
        fModel.build(TaskType.COMPILE);
    }

    @Test(dependsOnMethods = {"testCompile"})
    public void testTest() {
        fModel.build(TaskType.TEST);
    }
    
    @Test(dependsOnMethods = {"testCompile"})
    public void testPackage() {
        fModel.build(TaskType.PACKAGE);
    }


    public void testReports() {
        new VersionMgr().create("" + new Date());
        fModel.build(TaskType.REPORTS);
    }
}
