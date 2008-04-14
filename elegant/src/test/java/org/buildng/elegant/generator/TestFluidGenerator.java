package org.buildng.elegant.generator;

import static org.testng.Assert.*;

import org.apache.tools.ant.taskdefs.optional.dotnet.NetCommand;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Environment.Variable;
import org.testng.annotations.Test;


@Test
public class TestFluidGenerator {
    public void testGenerateFactory() {
        new FluidGenerator().generateElegant();
    }

    public void testApplicableConstructor() {
        FluidGenerator generator = new FluidGenerator();
        assertFalse(generator.hasApplicableNoArgsConstructor(Mapper.class));
        assertFalse(generator.hasApplicableNoArgsConstructor(NetCommand.class));
   }

    public void testhasNoCorrespondingAddConfiguredMethod() {
        assertFalse(FluidGenerator.hasNoCorrespondingAddConfiguredMethod(JUnitTask.class, "addSysproperty", Variable.class));
    }
}
