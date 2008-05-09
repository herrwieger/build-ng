package org.buildng.elegant.generator;

import static org.testng.Assert.*;

import java.io.File;

import org.apache.tools.ant.taskdefs.Javadoc.DocletInfo;
import org.apache.tools.ant.taskdefs.optional.dotnet.NetCommand;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Environment.Variable;
import org.testng.annotations.Test;


@Test
public class TestFluidGenerator {
    public void testGenerateFactory() {
        new FluidGenerator().generateElegant(new File("../elegant/src/main/java-gen"));
    }

    public void testApplicableConstructor() {
        assertFalse(FluidGenerator.hasApplicableNoArgsConstructor(Mapper.class));
        assertFalse(FluidGenerator.hasApplicableNoArgsConstructor(NetCommand.class));
   }

    public void testhasNoCorrespondingAddConfiguredMethod() {
        assertFalse(FluidGenerator.hasNoCorrespondingAddConfiguredMethod(JUnitTask.class, "addSysproperty", Variable.class));
    }
    
    public void testIsAntType() {
        assertTrue(AbstractMethodVisitor.isAntType(DocletInfo.class));
    }
}
