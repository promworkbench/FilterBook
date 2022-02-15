package org.processmining.filterbook.filters.project.global;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class EventLastEventGlobalAttributeTraceFilter extends EventLastEventGlobalAttributeFilter {

	public static final String NAME = "Project recurring on last event";

	public EventLastEventGlobalAttributeTraceFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public EventLastEventGlobalAttributeTraceFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	protected boolean isLast(XTrace trace, XEvent event, XAttribute attribute) {
		int i = trace.indexOf(event);
		if (i == trace.size() - 1) {
			return true;
		}
		for (int k = trace.size() - 1; k > i; k--) {
			if (attribute.equals(trace.get(k).getAttributes().get(attribute.getKey()))) {
				return false;
			}
		}
		return true;
	}
}
