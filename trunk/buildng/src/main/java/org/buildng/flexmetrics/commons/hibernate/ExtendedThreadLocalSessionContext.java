package org.buildng.flexmetrics.commons.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.context.ThreadLocalSessionContext;
import org.hibernate.engine.SessionFactoryImplementor;

/**
 * ThreadLocalSessionContext für das "Session per Conversation"-Pattern von
 * Hibernate.
 */
public class ExtendedThreadLocalSessionContext extends ThreadLocalSessionContext {
	private static final long serialVersionUID = -1888836183023370829L;

	/**
     * @see ThreadLocalSessionContext#ThreadLocalSessionContext(org.hibernate.engine.SessionFactoryImplementor)
     */
    public ExtendedThreadLocalSessionContext(SessionFactoryImplementor pFactory) {
        super(pFactory);
    }

    @Override
    protected Session buildOrObtainSession() {
        Session s = super.buildOrObtainSession();
//        s.setFlushMode(FlushMode.COMMIT);
        return s;
    }

    /**
     * Kein automatischer close() bei einem commit().
     * 
     * @return false
     */
    @Override
    protected boolean isAutoCloseEnabled() {
        return false;
    }

    @Override
    protected CleanupSynch buildCleanupSynch() {
        return new NoCleanupSynch(factory);
    }

    private static class NoCleanupSynch extends ThreadLocalSessionContext.CleanupSynch {
		private static final long serialVersionUID = -343942923142681315L;

		public NoCleanupSynch(SessionFactory pFactory) {
            super(pFactory);
        }

        public void beforeCompletion() {
        }

        public void afterCompletion(int pI) {
        }
    }

}
