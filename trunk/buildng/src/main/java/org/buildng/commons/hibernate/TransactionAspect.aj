package org.buildng.commons.hibernate;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;



public aspect TransactionAspect {
    // ---------------------------------------------------------
    // constants
    // ---------------------------------------------------------

	private static final Logger LOG = Logger.getLogger(TransactionAspect.class);

	
	
    // ---------------------------------------------------------
    // aspects
    // ---------------------------------------------------------

	pointcut transactionRequired(TransactionAttribute txAnnotation) :
        execution(* *(..)) && @annotation(txAnnotation) &&
        if(txAnnotation.value()==TransactionAttributeType.REQUIRED);
    
    Object around(TransactionAttribute txAnnotation) : transactionRequired(txAnnotation) {
        LOG.trace("Before " + thisJoinPoint.getSignature());
        
        Object 		result			= null;
        Transaction innerTx			= null;
        Session 	currentSession 	= HibernateUtil.getCurrentSession();
        try {
            if (!currentSession.getTransaction().isActive()) {
            	innerTx	= currentSession.beginTransaction();
            }
            result = proceed(txAnnotation);
        	if(innerTx!=null) {
        		innerTx.commit();
        	}
        } catch(RuntimeException ex) {
        	if(innerTx!=null) {
                innerTx.rollback();
        	}
        	throw ex;
        }
        LOG.trace("After " + thisJoinPoint.getSignature());
        return result;
    }
}
