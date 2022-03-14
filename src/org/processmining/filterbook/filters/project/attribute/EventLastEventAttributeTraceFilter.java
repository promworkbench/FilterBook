package org.processmining.filterbook.filters.project.attribute;

import java.util.Set;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeValueType;

public class EventLastEventAttributeTraceFilter extends EventLastEventAttributeFilter {

	public static final String NAME = "Project recurring on last event";

	public EventLastEventAttributeTraceFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public EventLastEventAttributeTraceFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	public boolean isLast(XTrace trace, XEvent event, XAttribute attribute, Set<AttributeValueType> values) {
		int i = trace.indexOf(event);
		if (i == trace.size() - 1) {
			return true;
		}
		for (int k = trace.size() - 1; k > i; k--) {
			if (values.contains(new AttributeValueType(trace.get(k).getAttributes().get(attribute.getKey())))) {
				return false;
			}
		}
		return true;
	}
}
