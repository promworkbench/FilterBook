package org.processmining.filterbook.filters.project.global;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class EventFirstLastEventGlobalAttributeTraceFilter extends EventFirstLastEventGlobalAttributeFilter {

	public static final String NAME = "Project recurring on first and last event";

	public EventFirstLastEventGlobalAttributeTraceFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public EventFirstLastEventGlobalAttributeTraceFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}
	
	public boolean isFirst(XTrace trace, XEvent event, XEventClassifier classifier) {
		int i = trace.indexOf(event);
		if (i == 0) {
			return true;
		}
		for (int k = 0; k < i; k++) {
			if (classifier.getClassIdentity(event).equals(classifier.getClassIdentity(trace.get(k)))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isLast(XTrace trace, XEvent event, XAttribute attribute) {
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
