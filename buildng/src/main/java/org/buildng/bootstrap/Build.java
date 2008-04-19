package org.buildng.bootstrap;

import org.buildng.builders.Compiler;
import org.buildng.builders.CompilerConfiguration;
import org.buildng.model.Model;
import org.buildng.model.Project;
import org.buildng.model.TaskType;


public class Build {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Model   model          = Model.createStandardModel("..", "lib");
        Project elegantProject = model.createProject("elegant")
            .addDependency("org.testng", "testng", "5.4-jdk15")
            .addDependency("org.apache.log4j", "log4j", "1.2.15")
            .addDependency("org.apache.commons", "commons-lang", "2.4")
            .addDependency("org.apache.ant", "ant", "1.7.0")
            .addDependency("org.apache.ant", "ant", "ant-antlr-1.7.0.jar")
            .addDependency("org.apache.ant", "ant", "ant-nodeps-1.7.0.jar")
            .addDependency("org.apache.ant", "ant", "ant-jdepend-1.7.0.jar")
            .addDependency("org.apache.ant", "ant", "ant-commons-net-1.7.0.jar")
            .addDependency("org.apache.ant", "ant", "ant-junit-1.7.0.jar")
            .addDependency("org.apache.ant", "ant", "ant-apache-oro-1.7.0.jar")
            .addDependency("org.apache.ant", "ant", "ant-jsch-1.7.0.jar")
            .addDependency("org.apache.ant", "ant", "ant-jmf-1.7.0.jar")
            .addDependency("org.apache.ant", "ant", "ant-swing-1.7.0.jar");
        CompilerConfiguration compilerConf   = new CompilerConfiguration()
                .sourceFolders("src/main/java", "src/main/java-gen")
                .testSourceFolders("src/test/java");
        Compiler              compiler       = new Compiler(compilerConf);
        elegantProject.putBuilderForTaskType(TaskType.COMPILE, compiler);
        
        model.build(TaskType.CLEAN, TaskType.COMPILE, TaskType.PACKAGE);
    }
}
