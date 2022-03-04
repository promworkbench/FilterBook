package org.processmining.filterbook.filters.project.global;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class EventFirstEventGlobalAttributeTraceFilter extends EventFirstEventGlobalAttributeFilter {

	public static final String NAME = "Project recurring on first event";

	public EventFirstEventGlobalAttributeTraceFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public EventFirstEventGlobalAttributeTraceFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}
	
	public boolean isFirst(XTrace trace, XEvent event, XAttribute attribute) {
		int i = trace.indexOf(event);
		if (i == 0) {
			return true;
		}
		for (int k = 0; k < i; k++) {
			if (attribute.equals(trace.get(k).getAttributes().get(attribute.getKey()))) {
				return false;
			}
		}
		return true;
	}
}
