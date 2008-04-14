package org.buildng.builders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.buildng.elegant.ElegantBuilder;
import org.buildng.elegant.type.PathTypeBuilder;
import org.buildng.model.Library;
import org.buildng.model.LibraryScope;
import org.buildng.model.Model;
import org.buildng.model.Project;


public class BuilderUtil {

    public static PathTypeBuilder createTestClasspath(ElegantBuilder pElegant, Model pModel, Project pProject,
            String pTargetFolder, String pTestTargetFolder, LibraryScope... pScopes) {

        PathTypeBuilder classpath = createClasspath(pElegant, pModel, pProject, pTargetFolder, pScopes);
        addProjectDependenciesToClasspath(pElegant, classpath, pProject, pTestTargetFolder);
        return classpath;
    }


    public static PathTypeBuilder createClasspath(ElegantBuilder pElegant, Model pModel, Project pProject,
            String pTargetFolder, LibraryScope... pScopes) {

        PathTypeBuilder classpath = pElegant.path();
        addProjectDependenciesToClasspath(pElegant, classpath, pProject, pTargetFolder);

        List<Library> libraryDependencies = new ArrayList<Library>();
        for (LibraryScope scope : pScopes) {
            libraryDependencies.addAll(pProject.getLibraryDependencies(scope));
        }
        addLibraryDependenciesToClasspath(pElegant, classpath, pModel, libraryDependencies);

        return classpath;
    }

    public static void addProjectDependenciesToClasspath(ElegantBuilder pElegant, PathTypeBuilder pPath, Project pProject, String pTargetFolder) {
        for (Project project : pProject.getProjectDependencies()) {
            pPath.add(pElegant.path().location(new File(project.getBaseDir(), pTargetFolder)));
        }
    }

    public static void addLibraryDependenciesToClasspath(ElegantBuilder pElegant, PathTypeBuilder pPath, Model pModel,
            List<Library> libraryDependencies) {
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
