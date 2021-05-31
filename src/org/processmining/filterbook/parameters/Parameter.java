package org.processmining.filterbook.parameters;

import javax.swing.JComponent;

import org.processmining.filterbook.filters.Filter;

public abstract class Parameter {
	
	/*
	 * Label for the parameter.
	 */
	private String label;
	
	/*
	 * Filter that contains this parameter.
	 */
	private Filter filter;
	
	/**
	 * This constructor exists for importing and exporting
	 */
	public Parameter( ) {
		
	}
	
	public Parameter(String label, Filter filter) {
		this.label = label;
		this.filter = filter;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean equals(Parameter param) {
	    if (param == null) {
	    	return false;
	    }
	    if (param == this) {
	    	return true;
	    }
	    if (param.getClass() == this.getClass()) {
	    	return true;
	    }
	    return false;
	}
	
	public abstract JComponent getWidget();

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}
}
