package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeType;

public class EventAttributeFilter extends EventGlobalAttributeFilter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Project events";

	public EventAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public EventAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}
	
	/**
	 * This filter is suitable if the log contains at least one event.
	 */
	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasEvents();
	}
	
	/**
	 * Make sure the attribute parameter is initialized.
	 */
	public void setAttributes(boolean doReset) {
		if (!doReset && getParameters().getOneFromListAttribute() != null) {
			return;
		}
		// Create a fresh set of attributes.
		Set<AttributeType> attrs = new TreeSet<AttributeType>();
		// Scan the entire log for attributes, store them in the fresh set.
		for (XTrace trace : getLog()) {
			for (XEvent event : trace) {
				for (XAttribute attribute : event.getAttributes().values()) {
					attrs.add(new AttributeType(attribute));
				}
			}
		}
		// Create a list from the attributes, and sort the list.
		List<AttributeType> attributes = new ArrayList<AttributeType>(attrs);
		Collections.sort(attributes);
		// Select the previously selected attribute, if possible.
		AttributeType selectedAttribute = attributes.isEmpty() ? null : attributes.get(0);
		if (getParameters().getOneFromListAttribute() != null
				&& attributes.contains(getParameters().getOneFromListAttribute().getSelected())) {
			selectedAttribute = attributes.get(attributes.indexOf(getParameters().getOneFromListAttribute().getSelected()));
		}
		// Set the new list.
		getParameters().setOneFromListAttribute(new OneFromListParameter<AttributeType>("Select an attribute", this,
				selectedAttribute, attributes, true));
	}

}
