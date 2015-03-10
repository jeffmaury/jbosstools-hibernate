package org.jboss.tools.hibernate.runtime.spi;

public interface IFacadeFactory {
	
	ClassLoader getClassLoader();
	IArtifactCollector createArtifactCollector();
	ICfg2HbmTool createCfg2HbmTool();
	INamingStrategy createNamingStrategy(Object target);
	IDialect createDialect(Object target);

}
