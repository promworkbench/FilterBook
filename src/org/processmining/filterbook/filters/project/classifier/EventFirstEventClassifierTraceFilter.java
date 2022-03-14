package org.processmining.filterbook.filters.project.classifier;

import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class EventFirstEventClassifierTraceFilter extends EventFirstEventClassifierFilter {

	public static final String NAME = "Project recurring on first event";

	public EventFirstEventClassifierTraceFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public EventFirstEventClassifierTraceFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	/*
	 * Returns whether the given event is the first event with the same value
	 * for the given classifier in the given trace.
	 */
	public boolean isFirst(XTrace trace, XEvent event, XEventClassifier classifier, Set<String> selectedValues) {
		int i = trace.indexOf(event);
		if (i == 0) {
			/*
			 * First event. Hence first.
			 */
			return true;
		}
		/*
		 * Check whether all previous events have different values.
		 */
		for (int k = 0; k < i; k++) {
			if (selectedValues.contains(classifier.getClassIdentity(trace.get(k)))) {
				/*
				 * Found previous event with the same value. Hence not first.
				 */
				return false;
			}
		}
		/*
		 * All previous events have different value. Hence first.
		 */
		return true;
	}
}
