package org.buildng.elegant;

import java.io.File;

import org.testng.annotations.Test;

@Test
public class TestElegant {

    public void testJavac() {
        ElegantBuilder elegant = new ElegantBuilder(new File("."));
        elegant.mkdir()
            .dir(new File("xtarget/classes"))
            .execute();
        elegant.javac()
            .addSrc(elegant.path().path("src/main/java"))
            .addSrc(elegant.path().path("src/main/java-gen"))
            .addSrc(elegant.path().path("src/test/java"))
            .destdir(new File("xtarget/classes"))
            .execute();
    }
}
