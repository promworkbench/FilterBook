package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeType;

public class TraceAttributeFilter extends TraceGlobalAttributeFilter {

	public static final String NAME = "Select on attribute value";

	public TraceAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public TraceAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	/**
	 * This filter is suitable if contains at least one trace an dif some trace contains at least one attribute.
	 */
	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasTraces() && hasTraceAttributes();
	}

	void setAttributes(boolean doReset) {
		if (!doReset && getParameters().getOneFromListAttribute() != null) {
			return;
		}
		Set<AttributeType> attrs = new TreeSet<AttributeType>();
		for (XTrace trace : getLog()) {
			for (XAttribute attribute : trace.getAttributes().values()) {
				attrs.add(new AttributeType(attribute));
			}
		}
		List<AttributeType> attributes = new ArrayList<AttributeType>(attrs);
		Collections.sort(attributes);
		AttributeType selectedAttribute = attributes.isEmpty() ? null : attributes.get(0);
		if (getParameters().getOneFromListAttribute() != null
				&& attributes.contains(getParameters().getOneFromListAttribute().getSelected())) {
			selectedAttribute = attributes
					.get(attributes.indexOf(getParameters().getOneFromListAttribute().getSelected()));
		}
		getParameters().setOneFromListAttribute(new OneFromListParameter<AttributeType>("Select an attribute",
				this, selectedAttribute, attributes, true));
	}
}
