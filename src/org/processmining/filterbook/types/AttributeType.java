package org.processmining.filterbook.types;

import org.deckfour.xes.model.XAttribute;

public class AttributeType implements Comparable<AttributeType> {

	/*
	 * Wrapper class for attributes.
	 */
	
	/**
	 * The attribute. Never null.
	 */
	private XAttribute attribute;

	public AttributeType(XAttribute attribute) {
		this.attribute = attribute;
	}

	public XAttribute getAttribute() {
		return attribute;
	}
	
	/**
	 * Name for the attribute type. Use attribute key.
	 */
	public String toString() {
		return attribute.getKey();
	}

	public int compareTo(AttributeType o) {
		if (attribute == null && o.attribute == null) {
			return 0;
		}
		if (attribute == null && o.attribute != null) {
			return -1;
		}
		if (o.attribute == null && attribute != null) {
			return 1;
		}
		return toString().compareTo(o.toString());
	}
	
	public boolean equals(Object o) {
		if (o instanceof AttributeType) {
			return toString().equals(o.toString());
		}
		return false;
	}
}
