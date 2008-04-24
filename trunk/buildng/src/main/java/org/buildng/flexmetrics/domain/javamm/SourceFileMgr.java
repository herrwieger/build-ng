package org.buildng.flexmetrics.domain.javamm;


import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.buildng.commons.hibernate.HibernateUtil;
import org.buildng.flexmetrics.domain.version.Version;
import org.hibernate.Query;


public class SourceFileMgr {

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public SourceFile findByNameAndVersion(String pName, Version pVersion) {
        Query query = HibernateUtil.getCurrentSession().createQuery(
                              "from SourceFile where name=:name and version=:version");
        query.setString("name", pName);
        query.setEntity("version", pVersion);
        return (SourceFile) query.uniqueResult();
    }
}
