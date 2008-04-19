package org.buildng.model;

/**
 * TaskType classifies builders. Its a symbolic name for the type of task a
 * builder should perform. A Project can have one specific Builder for each
 * TaskType. Builders of a project get later on invoked by their TaskType. For
 * example {@link #PACKAGE} could classify a JarPackager, WarPackager or
 * EarPackager. These Builders work differently, but all have the same type of
 * task in common: they package the compilation results of their project in an
 * artifact to be delivered. If you invoke {@link Model#build(TaskType)} the
 * model walks over each of its project and takes a builder of a project for the
 * given TaskType( {@link Project#getBuilderForTaskType(TaskType)}) and lets
 * that builder perform its specific task on the project.
 * 
 * @author thomas
 */
public enum TaskType {
    CLEAN,

    COMPILE, TEST, PACKAGE,

    SITE,
}
