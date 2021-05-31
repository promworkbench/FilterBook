package org.processmining.filterbook.filters;

import java.util.Set;
import java.util.TreeSet;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class EventFirstEventClassifierFilter extends EventClassifierFilter {
	public static final String NAME = "Project on first classifier value";

	public EventFirstEventClassifierFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public EventFirstEventClassifierFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}
	
	public XLog filter() {
		XLog filteredLog = initializeLog(getLog());
		XEventClassifier classifier = getParameters().getOneFromListClassifier().getSelected().getClassifier();
		Set<String> selectedValues = new TreeSet<String>(getParameters().getMultipleFromListString().getSelected());
		for (XTrace trace : getLog()) {
			XTrace filteredTrace = getFactory().createTrace(trace.getAttributes());
			for (XEvent event : trace) {
				String value = classifier.getClassIdentity(event);
				boolean match = selectedValues.contains(value);
				switch (getParameters().getOneFromListSelection().getSelected()) {
					case FILTERIN : {
						if (match) {
							if (isFirst(trace, event, classifier)) {
								filteredTrace.add(event);
							}
						} else {
							filteredTrace.add(event);
						}
						break;
					}
					case FILTEROUT : {
						if (match) {
							if (!isFirst(trace, event, classifier)) {
								filteredTrace.add(event);
							}
						}
						break;
					}
				}
			}
			filteredLog.add(filteredTrace);
		}
		return filteredLog;
	}
	
	private boolean isFirst(XTrace trace, XEvent event, XEventClassifier classifier) {
		int i = trace.indexOf(event);
		if (i == 0) {
			return true;
		}
		return !classifier.getClassIdentity(event).equals(classifier.getClassIdentity(trace.get(i - 1)));
	}
}
