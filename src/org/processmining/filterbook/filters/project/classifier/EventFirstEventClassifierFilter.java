package org.processmining.filterbook.filters.project.classifier;

import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.charts.DirectlyFollowsChart;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.SelectionType;

public class EventFirstEventClassifierFilter extends EventClassifierFilter {
	public static final String NAME = "Project series on first event";

	private XLog cachedLog;
	private XEventClassifier cachedClassifier;
	private Set<String> cachedSelectedValues;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;

	public EventFirstEventClassifierFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
		cachedLog = null;
	}

	public EventFirstEventClassifierFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
		cachedLog = null;
	}

	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		XEventClassifier classifier = (getParameters().getOneFromListClassifier().getSelected() != null
				? getParameters().getOneFromListClassifier().getSelected().getClassifier()
				: getDummyClassifier());
		Set<String> selectedValues = new TreeSet<String>(getParameters().getMultipleFromListStringA().getSelected());
		SelectionType selectionType = getParameters().getOneFromListSelection().getSelected();
		/*
		 * Check whether the cache is valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedClassifier.equals(classifier) && cachedSelectedValues.equals(selectedValues)
					&& cachedSelectionType == selectionType) {
				/*
				 * Yes, it is. Return the cached filtered log.
				 */
				System.out.println("[" + NAME + "]: Returning cached filtered log.");
				return cachedFilteredLog;
			}
		}
		/*
		 * No, it is not. Filter the log using the relevant parameters.
		 */
		System.out.println("[" + NAME + "]: Returning newly filtered log.");
		XLog filteredLog = initializeLog(getLog());
		for (XTrace trace : getLog()) {
			XTrace filteredTrace = getFactory().createTrace(trace.getAttributes());
			for (XEvent event : trace) {
				String value = classifier.getClassIdentity(event);
				boolean match = selectedValues.contains(value);
				switch (selectionType) {
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
		/*
		 * Update the cache and return the result.
		 */
		cachedLog = getLog();
		cachedClassifier = classifier;
		cachedSelectedValues = selectedValues;
		cachedSelectionType = selectionType;
		cachedFilteredLog = filteredLog;
		return filteredLog;
	}

	/*
	 * Returns whether the given event is the first of a series of events with
	 * the same value for the given classifier in the given trace.
	 */
	public boolean isFirst(XTrace trace, XEvent event, XEventClassifier classifier) {
		int i = trace.indexOf(event);
		if (i == 0) {
			/*
			 * First event. Hence first of a series.
			 */
			return true;
		}
		/*
		 * Return whether the previous event has a different value.
		 */
		return !classifier.getClassIdentity(event).equals(classifier.getClassIdentity(trace.get(i - 1)));
	}

	public JComponent getChartWidget() {
		return DirectlyFollowsChart.getChart(getLog(), getDummyClassifier(), getParameters());
	}

}
