package org.buildng.bootstrap;

import java.io.File;

import org.buildng.builders.AspectJCompiler;
import org.buildng.builders.Compiler;
import org.buildng.builders.CompilerConfiguration;
import org.buildng.builders.CompositeBuilder;
import org.buildng.defaults.Defaults;
import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.generator.FluidGenerator;
import org.buildng.model.Builder;
import org.buildng.model.LibraryScope;
import org.buildng.model.Model;
import org.buildng.model.Project;
import org.buildng.model.TaskType;


public class Build {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------

    private static final String SRC_MAIN_JAVA_GEN = "src/main/java-gen";


    
    //--------------------------------------------------------------------------  
    // class methods
    //--------------------------------------------------------------------------

    /**
     * @param args
     */
    public static void main(String[] args) {
        Model   model          = Defaults.createDefaultModel("..", "../buildng/lib");
        
        Project elegantGenProject = model.createProject("elegant-gen")
        .addDependency("testng-5.4-jdk15.jar")
        .addDependency("log4j-1.2.15.jar")
        .addDependency("commons/commons-lang-2.4.jar")
        .addDependency("ant/ant-1.7.0.jar")
        .addDependency("ant/ant-nodeps-1.7.0.jar")
        .addDependency("ant/ant-junit-1.7.0.jar");

        
        Project elegantProject = model.createProject("elegant")
            .addDependency(elegantGenProject)
            .addDependency("testng-5.4-jdk15.jar")
            .addDependency("log4j-1.2.15.jar")
            .addDependency("commons/commons-lang-2.4.jar")
            .addDependency("ant/ant-1.7.0.jar")
            .addDependency("ant/ant-antlr-1.7.0.jar")
            .addDependency("ant/ant-nodeps-1.7.0.jar")
            .addDependency("ant/ant-jdepend-1.7.0.jar")
            .addDependency("ant/ant-commons-net-1.7.0.jar")
            .addDependency("ant/ant-junit-1.7.0.jar")
            .addDependency("ant/ant-apache-oro-1.7.0.jar")
            .addDependency("ant/ant-jsch-1.7.0.jar")
            .addDependency("ant/ant-jmf-1.7.0.jar")
            .addDependency("ant/ant-swing-1.7.0.jar")
            .addDependency("aspectj/aspectjtools-1.6.0.jar");
        
        final CompilerConfiguration compilerConf   = new CompilerConfiguration()
                .sourceFolders("src/main/java", SRC_MAIN_JAVA_GEN)
                .testSourceFolders("src/test/java");
        Builder elegantGenerator = new Builder() {
            public void build(Model pModel, Project pProject) {
                ElegantBuilder elegant = new ElegantBuilder(pProject.getBaseDir());
                
                elegant.delete().dir(SRC_MAIN_JAVA_GEN).execute();
                
                new FluidGenerator().generateElegant(new File(pProject.getBaseDir(), SRC_MAIN_JAVA_GEN));
            }
        };
        Compiler              compiler       = new Compiler(compilerConf);
        elegantProject.putBuilderForTaskType(TaskType.COMPILE, new CompositeBuilder(elegantGenerator, compiler));

        Project buildngProject = model.createProject("buildng")
            .addDependency(elegantProject)
            .addDependency("aspectj/aspectjrt-1.6.0.jar")
            .addDependency("log4j-1.2.15.jar")
            .addDependency("commons/commons-lang-2.4.jar")
            .addDependency("ant/ant-1.7.0.jar")
            .addDependency("hibernate/hibernate-3.2.6.GA.jar")
            .addDependency("hibernate-annotations/hibernate-annotations-3.3.1.GA.jar")
            .addDependency("hibernate-annotations/hibernate-commmons-annotations-3.3.1.GA.jar")
            .addDependency("jee/jta-1.0.1.jar")
            .addDependency("jee/ejb-persistence-3.0.jar")
            .addDependency("jee/jee-5.0.jar")
            .addDependency("pmd/pmd-4.2.1.jar")
            .addDependency("testng-5.4-jdk15.jar", LibraryScope.TEST);
        AspectJCompiler              ajCompiler       = new AspectJCompiler(compilerConf);
        buildngProject.putBuilderForTaskType(TaskType.COMPILE, ajCompiler);
                
        model.build(TaskType.CLEAN, TaskType.COMPILE, TaskType.PACKAGE);
    }
}
