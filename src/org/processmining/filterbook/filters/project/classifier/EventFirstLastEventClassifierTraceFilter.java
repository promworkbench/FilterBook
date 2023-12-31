package org.processmining.filterbook.filters.project.classifier;

import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class EventFirstLastEventClassifierTraceFilter extends EventFirstLastEventClassifierFilter {

	public static final String NAME = "Project recurring on first and last event";

	public EventFirstLastEventClassifierTraceFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public EventFirstLastEventClassifierTraceFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}
	
	public boolean isFirst(XTrace trace, XEvent event, XEventClassifier classifier, Set<String> selectedValues) {
		int i = trace.indexOf(event);
		if (i == 0) {
			return true;
		}
		for (int k = 0; k < i; k++) {
			if (selectedValues.contains(classifier.getClassIdentity(trace.get(k)))) {
				return false;
			}
		}
		return true;
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
