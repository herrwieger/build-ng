package org.buildng.flexmetrics.domain.javamm;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.buildng.flexmetrics.domain.version.Version;


@Entity
public class MetaClass extends AbstractVersionedMetaData {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private SourceFile              fSourceFile;
    private MetaPackage             fMetaPackage;
    private Set<MetaField>          fMetaFields         = new HashSet<MetaField>();
    private Set<MetaConstructor>    fMetaConstructors   = new HashSet<MetaConstructor>();
    private Set<MetaMethod>         fMetaMethods        = new HashSet<MetaMethod>();

    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    MetaClass() {
    }
    
    public MetaClass(String pName, Version pVersion) {
        super(pName, pVersion);
    }


    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    @ManyToOne
    public MetaPackage getMetaPackage() {
        return fMetaPackage;
    }
    
    public void setMetaPackage(MetaPackage pMetaPackage) {
        fMetaPackage = pMetaPackage;
    }
    
    @ManyToOne
    public SourceFile getSourceFile() {
        return fSourceFile;
    }
    
    public void setSourceFile(SourceFile pSourceFile) {
        fSourceFile = pSourceFile;
    }

    
    @OneToMany(cascade = CascadeType.ALL)
    public Set<MetaConstructor> getMetaConstructors() {
        return fMetaConstructors;
    }

    void setMetaConstructors(Set<MetaConstructor> pMetaConstructors) {
        fMetaConstructors = pMetaConstructors;
    }
    
    public void addToMetaConstructors(MetaConstructor pConstructor) {
        fMetaConstructors.add(pConstructor);
    }

    
    @OneToMany(cascade = CascadeType.ALL)
    public Set<MetaField> getMetaFields() {
        return fMetaFields;
    }

    void setMetaFields(Set<MetaField> pMetaFields) {
        fMetaFields = pMetaFields;
    }

    public void addToMetaFields(MetaField pField) {
        fMetaFields.add(pField);
    }
    
    
    @OneToMany(cascade = CascadeType.ALL)
    public Set<MetaMethod> getMetaMethods() {
        return fMetaMethods;
    }

    void setMetaMethods(Set<MetaMethod> pMetaMethods) {
        fMetaMethods = pMetaMethods;
    }
    
    public void addToMetaMethods(MetaMethod pMethod) {
        fMetaMethods.add(pMethod);
    }

    public MetaMethod getBestMatch(String pMethodname, int pLine) {
        for (MetaMethod method : fMetaMethods) {
            if (method.matches(pMethodname, pLine)) {
                return method;
            }
        }
        return null;
    }    
}
