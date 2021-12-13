package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.MultipleFromListParameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.SelectionType;

public class TraceLastEventClassifierFilter extends EventClassifierFilter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select on last classifier value";

	private XLog cachedLog;
	private XEventClassifier cachedClassifier;
	private Set<String> cachedSelectedValues;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;

	/**
	 * Construct a start event filter for the given log and the given parameters. If
	 * required parameters are set to null, they will be properly initialized using
	 * default values.
	 * 
	 * @param log
	 *            The log to filter.
	 * @param parameters
	 *            The parameters to use while filtering.
	 */
	public TraceLastEventClassifierFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
		cachedLog = null;
	}

	public TraceLastEventClassifierFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
		cachedLog = null;
	}

	/**
	 * Filter the set log on the events using the set parameters.
	 */
	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		XEventClassifier classifier = (getParameters().getOneFromListClassifier().getSelected() != null
				? getParameters().getOneFromListClassifier().getSelected().getClassifier()
				: getDummyClassifier());
		Set<String> selectedValues = new HashSet<String>(getParameters().getMultipleFromListStringA().getSelected());
		SelectionType selectionType = getParameters().getOneFromListSelection().getSelected();
		/*
		 * Check whether the cache is  valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedClassifier.equals(classifier) &&
					cachedSelectedValues.equals(selectedValues) &&
					cachedSelectionType == selectionType) {
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
			boolean match = false;
			if (!trace.isEmpty()) {
				String value = classifier.getClassIdentity(trace.get(trace.size() - 1));
				match = selectedValues.contains(value);
			}
			switch (selectionType) {
				case FILTERIN : {
					if (match) {
						filteredLog.add(trace);
					}
					break;
				}
				case FILTEROUT : {
					if (!match) {
						filteredLog.add(trace);
					}
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
	
	/*
	 * Make sure the attribute values parameter is initialized.
	 */
	void setAttributeValues(boolean doReset) {
		if (!doReset && getParameters().getMultipleFromListStringA() != null) {
			return;
		}
		XEventClassifier classifier;
		if (getParameters().getOneFromListClassifier() == null || getParameters().getOneFromListClassifier().getSelected() == null) {
			classifier = new XEventNameClassifier();
		} else {
			classifier = getParameters().getOneFromListClassifier().getSelected().getClassifier();
		}
		Set<String> values = new HashSet<String>();
		for (XTrace trace : getLog()) {
			if (!trace.isEmpty()) {
				values.add(classifier.getClassIdentity(trace.get(trace.size() - 1)));
			}
		}
		List<String> unsortedValues = new ArrayList<String>(values);
		List<String> selectedValues = new ArrayList<String>(values);
		if (getParameters().getMultipleFromListStringA() != null) {
			selectedValues.retainAll(getParameters().getMultipleFromListStringA().getSelected());
		}
		getParameters().setMultipleFromListStringA(
				new MultipleFromListParameter<String>("Select values", this, selectedValues, unsortedValues, true));
	}

}

