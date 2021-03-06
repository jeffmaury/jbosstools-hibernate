package org.jboss.tools.hibernate.runtime.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public abstract class AbstractForeignKeyFacade 
extends AbstractFacade 
implements IForeignKey {

	private ITable referencedTable = null;
	private HashSet<IColumn> columns = null;
	private List<IColumn> referencedColumns = null;

	public AbstractForeignKeyFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}	

	@Override
	public ITable getReferencedTable() {
		if (referencedTable == null) {
			Object targetReferencedTable = Util.invokeMethod(
					getTarget(), 
					"getReferencedTable", 
					new Class[] {}, 
					new Object[] {});
			if (targetReferencedTable != null) {
				referencedTable = getFacadeFactory().createTable(targetReferencedTable);
			}
		}
		return referencedTable;
	}

	@Override
	public Iterator<IColumn> columnIterator() {
		if (columns == null) {
			initializeColumns();
		}
		return columns.iterator();
	}
	
	@Override
	public boolean isReferenceToPrimaryKey() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isReferenceToPrimaryKey", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public List<IColumn> getReferencedColumns() {
		if (referencedColumns == null) {
			initializeReferencedColumns();
		}
		return referencedColumns;
	}
	
	@Override
	public boolean containsColumn(IColumn column) {
		Object columnTarget = Util.invokeMethod(
				column, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"containsColumn", 
				new Class[] { getColumnClass() }, 
				new Object[] { columnTarget });
	}
	
	protected Class<?> getColumnClass() {
		return Util.getClass(getColumnClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getColumnClassName() {
		return "org.hibernate.mapping.Column";
	}

	protected void initializeColumns() {
		columns = new HashSet<IColumn>();
		Iterator<?> origin = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"columnIterator", 
				new Class[] {}, 
				new Object[] {});
		while (origin.hasNext()) {
			columns.add(getFacadeFactory().createColumn(origin.next()));
		}
	}

	protected void initializeReferencedColumns() {
		referencedColumns = new ArrayList<IColumn>();
		List<?> targetReferencedColumns = (List<?>)Util.invokeMethod(
				getTarget(), 
				"getReferencedColumns", 
				new Class[] {}, 
				new Object[] {});
		for (Object column : targetReferencedColumns) {
			referencedColumns.add(getFacadeFactory().createColumn(column));
		}
	}

}
