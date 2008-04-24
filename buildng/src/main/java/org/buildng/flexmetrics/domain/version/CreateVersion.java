package org.buildng.flexmetrics.domain.version;


public class CreateVersion {
    public static void main(String[] args) {
        if (args.length!=1) {
            throw new IllegalArgumentException("expected label argument");
        }
        new VersionMgr().create(args[0]);
    }
}
