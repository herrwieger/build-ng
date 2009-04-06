package org.buildng.builders;

import java.io.File;
import java.util.Collection;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.type.PathTypeBuilder;
import org.buildng.model.Library;
import org.buildng.model.RepositoryLibrary;
import org.buildng.model.LibraryScope;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class BuilderUtil {
    public static PathTypeBuilder createTestRuntimeClasspath(ElegantBuilder pElegant, Model pModel, Project pProject,
            String pTargetFolder, String pTestTargetFolder, LibraryScope ... pScopes) {
        
        PathTypeBuilder classpath = createMainRuntimeClasspath(pElegant, pModel, pProject, pTargetFolder, pTestTargetFolder, pScopes);
        addTargetFoldersOfDependendProjectsToClasspath(pElegant, classpath, pProject.getTransitiveProjectDependencies(), pTestTargetFolder);
        
        return classpath;
    }
    
    public static PathTypeBuilder createMainRuntimeClasspath(ElegantBuilder pElegant, Model pModel, Project pProject,
            String pTargetFolder, String pTestTargetFolder, LibraryScope ... pScopes) {
        
        PathTypeBuilder classpath = pElegant.path();
        
        addLibraryDependenciesToClasspath(pElegant, classpath, pModel,
                pProject.getTransitiveLibraryDependencies(pScopes));
        addTargetFoldersOfDependendProjectsToClasspath(pElegant, classpath, pProject.getTransitiveProjectDependencies(), pTargetFolder);
        
        return classpath;
    }
    
    
    public static PathTypeBuilder createTestCompileClasspath(ElegantBuilder pElegant, Model pModel, Project pProject,
            String pTargetFolder, String pTestTargetFolder, LibraryScope... pScopes) {

        PathTypeBuilder classpath = createMainCompileClasspath(pElegant, pModel, pProject, pTargetFolder, pScopes);
        addTargetFoldersOfDependendProjectsToClasspath(pElegant, classpath, pProject.getProjectDependencies(), pTestTargetFolder);
        return classpath;
    }


    public static PathTypeBuilder createMainCompileClasspath(ElegantBuilder pElegant, Model pModel, Project pProject,
            String pTargetFolder, LibraryScope... pScopes) {

        PathTypeBuilder classpath = pElegant.path();
        addTargetFoldersOfDependendProjectsToClasspath(pElegant, classpath, pProject.getProjectDependencies(),
                pTargetFolder);
        addLibraryDependenciesToClasspath(pElegant, classpath, pModel, pProject.getLibraryDependencies(pScopes));

        return classpath;
    }

    
    public static void addTargetFoldersOfDependendProjectsToClasspath(ElegantBuilder pElegant, PathTypeBuilder pPath,
            Collection<Project> pProjectDependencies, String pTargetFolder) {
        for (Project project : pProjectDependencies) {
            pPath.add(pElegant.path().location(new File(project.getBaseDir(), pTargetFolder)));
        }
    }

    public static void addLibraryDependenciesToClasspath(ElegantBuilder pElegant, PathTypeBuilder pPath, Model pModel,
            Collection<Library> libraryDependencies) {
        for (Library library : libraryDependencies) {
            pPath.add(pElegant.path().location(library.getFile(pModel.getRepositoryDir())));
        }
    }

    public static void createFolders(ElegantBuilder pElegant, String... pFolders) {
        for (String folder : pFolders) {
            pElegant.mkdir()
                .dir(folder)
                .execute();
        }
    }
}
