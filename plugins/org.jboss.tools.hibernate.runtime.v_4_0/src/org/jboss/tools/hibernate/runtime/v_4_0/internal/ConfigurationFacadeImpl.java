package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.util.HashSet;
import java.util.Iterator;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMapping;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.xml.sax.EntityResolver;

public class ConfigurationFacadeImpl extends AbstractConfigurationFacade {
	
	private HashSet<ITable> tableMappings = null;
	private ServiceRegistry serviceRegistry = null;
	private IMapping mapping = null;
	private IDialect dialect = null;

	
	public ConfigurationFacadeImpl(
			IFacadeFactory facadeFactory, 
			Configuration configuration) {
		super(facadeFactory, configuration);
	}
	
	public Configuration getTarget() {
		return (Configuration)super.getTarget();
	}

	protected Object buildTargetSessionFactory() {
		if (serviceRegistry == null) {
			buildServiceRegistry();
		}
		return getTarget().buildSessionFactory(serviceRegistry);
	}

	protected Object buildTargetSettings() {
		if (serviceRegistry == null) {
			buildServiceRegistry();
		}
		return getTarget().buildSettings(serviceRegistry);
	}
	
	@Override
	public void readFromJDBC() {
		if (getTarget() instanceof JDBCMetaDataConfiguration) {
			((JDBCMetaDataConfiguration)getTarget()).readFromJDBC();
		}
	}

	@Override
	public IMapping buildMapping() {
		if (mapping == null) {
			Mapping m = getTarget().buildMapping();
			if (m != null) {
				mapping = getFacadeFactory().createMapping(m);
			}
		}
		return mapping;
	}

	@Override
	public IPersistentClass getClassMapping(String string) {
		if (classMappings == null) {
			initializeClassMappings();
		}
		return classMappings.get(string);
	}

	@Override
	public INamingStrategy getNamingStrategy() {
		if (namingStrategy == null) {
			namingStrategy = getFacadeFactory().createNamingStrategy(getTarget().getNamingStrategy());
		}
		return namingStrategy;
	}

	@Override
	public EntityResolver getEntityResolver() {
		return getTarget().getEntityResolver();
	}

	@Override
	public Iterator<ITable> getTableMappings() {
		Iterator<ITable> result = null;
		if (getTarget() instanceof JDBCMetaDataConfiguration) {
			if (tableMappings == null) {
				initializeTableMappings();
			}
			result = tableMappings.iterator();
		}
		return result;
	}
	
	private void initializeTableMappings() {
		Iterator<Table> iterator = ((JDBCMetaDataConfiguration)getTarget()).getTableMappings();
		while (iterator.hasNext()) {
			tableMappings.add(getFacadeFactory().createTable(iterator.next()));
		}
	}

	@Override
	public IDialect getDialect() {
		if (dialect != null) {
			DialectFactory dialectFactory = serviceRegistry.getService(DialectFactory.class);
			Dialect d = dialectFactory.buildDialect(getProperties(), null);
			if (d != null) {
				dialect = getFacadeFactory().createDialect(d);
			}
		}
		return dialect;
	}
	
	private void buildServiceRegistry() {
		ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
		builder.applySettings(getTarget().getProperties());
		serviceRegistry = builder.buildServiceRegistry();		
	}

}
