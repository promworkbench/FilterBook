package org.processmining.filterbook.types;

import org.deckfour.xes.model.XAttribute;

public class AttributeValueType implements Comparable<AttributeValueType> {

	/*
	 * Wrapper class for attribute vluae.
	 */
	
	/**
	 * The attribute. May be null.
	 */
	private XAttribute attribute;

	public AttributeValueType(XAttribute attribute) {
		this.attribute = attribute;
	}

	public XAttribute getAttribute() {
		return attribute;
	}
	
	/**
	 * Name for the attribute value type. Use attribute value.
	 */
	public String toString() {
		return attribute == null ? "(NULL)" : attribute.toString();
	}

	public int compareTo(AttributeValueType o) {
		if (attribute == null && o.attribute == null) {
			return 0;
		}
		if (attribute == null && o.attribute != null) {
			return -1;
		}
		if (o.attribute == null && attribute != null) {
			return 1;
		}
		return attribute.compareTo(o.attribute);
	}
	
	public boolean equals(Object o) {
		if (o instanceof AttributeValueType) {
			if (attribute == null) {
				return ((AttributeValueType) o).attribute == null;
			}
			return attribute.equals(((AttributeValueType) o).attribute);
		}
		return false;
	}
}
