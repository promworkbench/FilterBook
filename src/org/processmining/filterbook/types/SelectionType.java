package org.processmining.filterbook.types;

public enum SelectionType {
	FILTERIN("Filter in"),
	FILTEROUT("Filter out");
	
	private String label;
	
	private SelectionType(String label) {
		this.label = label;
	}

	public String toString() {
		return label;
	}
}
