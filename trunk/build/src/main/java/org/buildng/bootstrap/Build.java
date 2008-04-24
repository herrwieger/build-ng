package org.buildng.bootstrap;

import java.io.File;

import org.buildng.builders.Compiler;
import org.buildng.builders.CompilerConfiguration;
import org.buildng.builders.CompositeBuilder;
import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.generator.FluidGenerator;
import org.buildng.model.Builder;
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
        Model   model          = Model.createStandardModel("..", "../buildng/lib");
        
        Project elegantGenProject = model.createProject("elegant-gen")
        .addDependency("org.testng", "testng", "5.4-jdk15")
        .addDependency("org.apache.log4j", "log4j", "1.2.15")
        .addDependency("org.apache.commons", "commons-lang", "2.4")
        .addDependency("org.apache.ant", "ant", "1.7.0")
        .addSecondaryDependency("org.apache.ant", "ant", "1.7.0", "ant-nodeps")
        .addSecondaryDependency("org.apache.ant", "ant", "1.7.0", "ant-junit");

        
        Project elegantProject = model.createProject("elegant")
            .addDependency(elegantGenProject)
            .addDependency("org.testng", "testng", "5.4-jdk15")
            .addDependency("org.apache.log4j", "log4j", "1.2.15")
            .addDependency("org.apache.commons", "commons-lang", "2.4")
            .addDependency("org.apache.ant", "ant", "1.7.0")
            .addSecondaryDependency("org.apache.ant", "ant", "1.7.0", "ant-antlr")
            .addSecondaryDependency("org.apache.ant", "ant", "1.7.0", "ant-nodeps")
            .addSecondaryDependency("org.apache.ant", "ant", "1.7.0", "ant-jdepend")
            .addSecondaryDependency("org.apache.ant", "ant", "1.7.0", "ant-commons-net")
            .addSecondaryDependency("org.apache.ant", "ant", "1.7.0", "ant-junit")
            .addSecondaryDependency("org.apache.ant", "ant", "1.7.0", "ant-apache-oro")
            .addSecondaryDependency("org.apache.ant", "ant", "1.7.0", "ant-jsch")
            .addSecondaryDependency("org.apache.ant", "ant", "1.7.0", "ant-jmf")
            .addSecondaryDependency("org.apache.ant", "ant", "1.7.0", "ant-swing");
        
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

        model.createProject("buildng")
            .addDependency(elegantProject)
            .addDependency("org.testng", "testng", "5.4-jdk15")
            .addDependency("org.apache.log4j", "log4j", "1.2.15")
            .addDependency("org.apache.commons", "commons-lang", "2.4")
            .addDependency("org.apache.ant", "ant", "1.7.0");
        
        model.build(TaskType.CLEAN, TaskType.COMPILE, TaskType.PACKAGE);
    }
}
