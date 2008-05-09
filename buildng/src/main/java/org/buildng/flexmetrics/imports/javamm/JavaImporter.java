package org.buildng.flexmetrics.imports.javamm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.buildng.builders.JavadocBuilder;
import org.buildng.commons.hibernate.HibernateUtil;
import org.buildng.flexmetrics.domain.javamm.MetaClass;
import org.buildng.flexmetrics.domain.javamm.MetaConstructor;
import org.buildng.flexmetrics.domain.javamm.MetaField;
import org.buildng.flexmetrics.domain.javamm.MetaMethod;
import org.buildng.flexmetrics.domain.javamm.MetaPackage;
import org.buildng.flexmetrics.domain.javamm.SourceFile;
import org.buildng.flexmetrics.domain.project.Project;
import org.buildng.flexmetrics.domain.project.ProjectMgr;
import org.buildng.flexmetrics.domain.version.Version;
import org.buildng.flexmetrics.domain.version.VersionMgr;
import org.buildng.flexmetrics.imports.util.ImportUtil;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;

public class JavaImporter {
	// --------------------------------------------------------------------------
	// class variables
	// --------------------------------------------------------------------------

	private static final Logger LOG = Logger.getLogger(JavaImporter.class);

	
	
	// --------------------------------------------------------------------------
	// instance variables
	// --------------------------------------------------------------------------

	private Version                        fCurrentVersion;
    private Project                        fCurrentProject;
    private final Map<String, MetaPackage> fMetaPackagesByName = new HashMap<String, MetaPackage>();
    private final Map<String, SourceFile>  fSourceFileNames    = new HashMap<String, SourceFile>();

	
	
	// --------------------------------------------------------------------------
	// static methods
	// --------------------------------------------------------------------------

	public static boolean start(RootDoc root) {
		HibernateUtil.getSessionFactory();
		new JavaImporter().run(root);
		return true;
	}

	
	
	// --------------------------------------------------------------------------
	// Doclet convention methods
	// --------------------------------------------------------------------------

	@TransactionAttribute(value = TransactionAttributeType.REQUIRED)
	private void run(RootDoc pRootDoc) {
		fCurrentVersion = new VersionMgr().getCurrent();
		fCurrentProject = new ProjectMgr().getOrCreateProjectNamed(getProjectOption(pRootDoc.options()));

		ClassDoc[] classDocs = pRootDoc.classes();
		for (ClassDoc classDoc : classDocs) {
			reportClassDoc(classDoc);
		}
	}

	 public static int optionLength(String option) {
        if (option.equals(JavadocBuilder.PROJECT_OPTION)) {
            return 2;
        }
        return 0;
    }
	
	 private static String getProjectOption(String[][] options) {
        for (int i = 0; i < options.length; i++) {
            String[] opt = options[i];
            if (opt[0].equals(JavadocBuilder.PROJECT_OPTION)) {
                return opt[1];
            }
        }
        return null;
    }
	 
	 
	//--------------------------------------------------------------------------  
    // instance methods
    //--------------------------------------------------------------------------

	private void reportClassDoc(ClassDoc pClassDoc) {
		MetaPackage metaPackage = getOrCreatePackage(pClassDoc.containingPackage());
        SourceFile  sourceFile  = getOrCreateSourceFile(pClassDoc.position().file());
        MetaClass   metaClass   = new MetaClass(pClassDoc.name(), fCurrentVersion);
        metaPackage.addToMetaClasses(metaClass);
        metaClass.setMetaPackage(metaPackage);
        sourceFile.addToMetaClasses(metaClass);
        metaClass.setSourceFile(sourceFile);

        for (FieldDoc fieldDoc : pClassDoc.fields(false)) {
            reportField(metaClass, fieldDoc);
        }
        for (ConstructorDoc constructorDoc : pClassDoc.constructors(false)) {
            reportConstructorDoc(metaClass, constructorDoc);
        }
        for (MethodDoc methodDoc : pClassDoc.methods(false)) {
            reportMethod(metaClass, methodDoc);
        }
	}

	private MetaPackage getOrCreatePackage(PackageDoc pPackageDoc) {
		String      packageName = pPackageDoc.name();
        MetaPackage metaPackage = fMetaPackagesByName.get(packageName);
        if (metaPackage != null) {
            return metaPackage;
        }
        metaPackage = new MetaPackage(packageName, fCurrentVersion);
        fMetaPackagesByName.put(packageName, metaPackage);
        HibernateUtil.save(metaPackage);
        return metaPackage;
	}

	private SourceFile getOrCreateSourceFile(File pFile) {
		String     fileName   = getFileName(pFile);
                   fileName   = ImportUtil.getNormalizedFilename(fileName, "java");
        SourceFile sourceFile = fSourceFileNames.get(fileName);
        if (sourceFile != null) {
            return sourceFile;
        }
        sourceFile = new SourceFile(fileName, fCurrentProject, fCurrentVersion);
        HibernateUtil.save(sourceFile);
        return sourceFile;
	}

	private String getFileName(File pFile) {
		String fileName;
        try {
            fileName = pFile.getCanonicalPath();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return fileName;
	}

	private void reportField(MetaClass pMetaClass, FieldDoc pFieldDoc) {
		SourcePosition position = pFieldDoc.position();
        LOG.debug(pFieldDoc + "," + toString(position));
        MetaField metaField = new MetaField(pFieldDoc.name(), position.line(), position.column());
        pMetaClass.addToMetaFields(metaField);
        metaField.setContainingMetaClass(pMetaClass);
	}

	private void reportConstructorDoc(MetaClass pMetaClass,
			ConstructorDoc pConstructorDoc) {
		SourcePosition position = pConstructorDoc.position();
        LOG.debug(pConstructorDoc + "," + toString(position));
        MetaConstructor metaConstructor = new MetaConstructor(pConstructorDoc.name(), position.line(),
                                                  position.column());
        pMetaClass.addToMetaConstructors(metaConstructor);
        metaConstructor.setContainingMetaClass(pMetaClass);
	}

	private void reportMethod(MetaClass pMetaClass, MethodDoc pMethodDoc) {
		SourcePosition position = pMethodDoc.position();
        LOG.debug(pMethodDoc + "," + toString(position));
        MetaMethod metaMethod = new MetaMethod(pMethodDoc.name(), position.line(), position.column());
        pMetaClass.addToMetaMethods(metaMethod);
        metaMethod.setContainingMetaClass(pMetaClass);
	}

	private String toString(SourcePosition pPosition) {
		return pPosition.file() + "," + pPosition.line() + ","
				+ pPosition.column();
	}
}
