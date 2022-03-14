package org.processmining.filterbook.filters.project.classifier;

import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class EventLastEventClassifierTraceFilter extends EventLastEventClassifierFilter {

	public static final String NAME = "Project recurring on last event";

	public EventLastEventClassifierTraceFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public EventLastEventClassifierTraceFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}
	
	public boolean isLast(XTrace trace, XEvent event, XEventClassifier classifier, Set<String> selectedValues) {
		int i = trace.indexOf(event);
		if (i == trace.size() - 1) {
			return true;
		}
		for (int k = trace.size() - 1; k > i; k--) {
			if (selectedValues.contains(classifier.getClassIdentity(trace.get(k)))) {
				return false;
			}
		}
		return true;
	}
}
