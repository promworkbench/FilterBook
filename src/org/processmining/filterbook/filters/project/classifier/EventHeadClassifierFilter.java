package org.processmining.filterbook.filters.project.classifier;

import java.util.Set;
import java.util.TreeSet;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.SelectionType;

public class EventHeadClassifierFilter extends EventClassifierFilter {
	public static final String NAME = "Project on minimal suffix";

	private XLog cachedLog;
	private XEventClassifier cachedClassifier;
	private Set<String> cachedSelectedValues;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;

	public EventHeadClassifierFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
		cachedLog = null;
	}

	public EventHeadClassifierFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
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
			/*
			 * filteredInTrace will contain all events as from (and including)
			 * the first event that matches.
			 */
			XTrace filteredInTrace = getFactory().createTrace(trace.getAttributes());
			/*
			 * filteredOutTrace will contain all event up to (and excluding) the
			 * first event that matches.
			 */
			XTrace filteredOutTrace = getFactory().createTrace(trace.getAttributes());
			for (XEvent event : trace) {
				String value = classifier.getClassIdentity(event);
				boolean match = selectedValues.contains(value);
				if (match || !filteredInTrace.isEmpty()) {
					/*
					 * Either this is the first match, or a previous event matched.
					 * Add to filterdInTrace.
					 */
					filteredInTrace.add(event);
				} else {
					/*
					 * This is not a match, and previous events also did not match.
					 */
					filteredOutTrace.add(event);
				}
			}
			switch (selectionType) {
				case FILTERIN : {
					/*
					 * Return the tail of the trace that starts with the first match. 
					 */
					filteredLog.add(filteredInTrace);
					break;
				}
				case FILTEROUT : {
					/*
					 * Return the head of the trace that ends just before the first match.
					 */
					filteredLog.add(filteredOutTrace);
					break;
				}
			}
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
}
