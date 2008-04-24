package org.buildng.flexmetrics.imports.javamm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.buildng.commons.hibernate.HibernateUtil;
import org.buildng.flexmetrics.domain.javamm.MetaClass;
import org.buildng.flexmetrics.domain.javamm.MetaConstructor;
import org.buildng.flexmetrics.domain.javamm.MetaField;
import org.buildng.flexmetrics.domain.javamm.MetaMethod;
import org.buildng.flexmetrics.domain.javamm.MetaPackage;
import org.buildng.flexmetrics.domain.javamm.SourceFile;
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

    private static final Logger            LOG                 = Logger.getLogger(JavaImporter.class);



    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

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
    // JavaReports methods
    // --------------------------------------------------------------------------

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    private void run(RootDoc pRootDoc) {
        Version version = new VersionMgr().getCurrent();

        ClassDoc[] classDocs = pRootDoc.classes();
        for (ClassDoc classDoc : classDocs) {
            reportClassDoc(version, classDoc);
        }
    }


    private void reportClassDoc(Version pVersion, ClassDoc pClassDoc) {
        MetaPackage metaPackage = getOrCreatePackage(pVersion, pClassDoc.containingPackage());
        SourceFile  sourceFile  = getOrCreateSourceFile(pVersion, pClassDoc.position().file());
        MetaClass   metaClass   = new MetaClass(pClassDoc.name(), pVersion);
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

    private MetaPackage getOrCreatePackage(Version pVersion, PackageDoc pPackageDoc) {
        String      packageName = pPackageDoc.name();
        MetaPackage metaPackage = fMetaPackagesByName.get(packageName);
        if (metaPackage != null) {
            return metaPackage;
        }
        metaPackage = new MetaPackage(packageName, pVersion);
        fMetaPackagesByName.put(packageName, metaPackage);
        HibernateUtil.save(metaPackage);
        return metaPackage;
    }

    private SourceFile getOrCreateSourceFile(Version pVersion, File pFile) {
        String     fileName   = getFileName(pFile);
                   fileName   = ImportUtil.getNormalizedFilename(fileName, "java");
        SourceFile sourceFile = fSourceFileNames.get(fileName);
        if (sourceFile != null) {
            return sourceFile;
        }
        sourceFile = new SourceFile(fileName, pVersion);
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


    private void reportConstructorDoc(MetaClass pMetaClass, ConstructorDoc pConstructorDoc) {
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
        return pPosition.file() + "," + pPosition.line() + "," + pPosition.column();
    }
}
