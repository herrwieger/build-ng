package org.buildng.flexmetrics.domain.javamm;

import javax.persistence.Entity;

@Entity
public class MetaConstructor extends MetaMember {
    MetaConstructor() {
    }
    
    public MetaConstructor(String pName, int pLine, int pColumn) {
        super(pName, pLine, pColumn);
    }
}
