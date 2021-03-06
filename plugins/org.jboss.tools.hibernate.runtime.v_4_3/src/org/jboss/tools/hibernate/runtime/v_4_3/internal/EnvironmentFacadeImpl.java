package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractEnvironmentFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class EnvironmentFacadeImpl extends AbstractEnvironmentFacade {

	public EnvironmentFacadeImpl(
			IFacadeFactory facadeFactory) {
		super(facadeFactory, null);
	}

	@Override
	public String getTransactionManagerStrategy() {
		// Unsupported in 4.3 Environment
		return "hibernate.transaction.manager_lookup_class";
	}

}
