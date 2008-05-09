package org.buildng.flexmetrics.domain.version;


import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.buildng.commons.hibernate.HibernateUtil;
import org.hibernate.Query;


public class VersionMgr {

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public Version findById(int pId) {
        return (Version) HibernateUtil.getCurrentSession().get(Version.class, Integer.valueOf(pId));
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public Version getCurrent() {
        Query query = HibernateUtil.getCurrentSession().createQuery("from Version where current = true");
        return (Version) query.uniqueResult();
    }


    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void create(String pLabel) {
        Version currentVersion = getCurrent();
        if (currentVersion != null) {
            currentVersion.setCurrent(false);
        }
        Version nextVersion = new Version(pLabel);
        HibernateUtil.save(nextVersion);
    }
}
