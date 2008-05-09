package org.buildng.elegant.generator;

import static org.testng.Assert.*;

import org.apache.tools.ant.taskdefs.Javadoc.DocletInfo;
import org.testng.annotations.Test;


@Test
public class TestAbstractMethodVisitor {

    public void testNeedsABuilder() {
        assertTrue(AbstractMethodVisitor.isAntTaskOrType(DocletInfo.class));
    }
}
