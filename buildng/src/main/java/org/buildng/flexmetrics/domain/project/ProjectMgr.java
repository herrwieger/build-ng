package org.buildng.flexmetrics.domain.project;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.buildng.commons.hibernate.HibernateUtil;

public class ProjectMgr {
	@TransactionAttribute(value = TransactionAttributeType.REQUIRED)
	public Project findByName(String pName) {
		return (Project) HibernateUtil.getCurrentSession().createQuery(
				"from Project where name=?").setString(0, pName).uniqueResult();
	}
	

	@TransactionAttribute(value = TransactionAttributeType.REQUIRED)
	public void create(String pName) {
		Project newProject = new Project(pName);
		HibernateUtil.save(newProject);
	}


    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public Project getOrCreateProjectNamed(String pName) {
        Project project = findByName(pName);
        if (project != null) {
            return project;
        }
        project = new Project(pName);
        HibernateUtil.save(project);
        return project;
    }
}
