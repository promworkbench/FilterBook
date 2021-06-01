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

public class TraceLastEventClassifierFilter extends EventClassifierFilter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select on last classifier value";

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
	}

	public TraceLastEventClassifierFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	/**
	 * Filter the set log on the events using the set parameters.
	 */
	public XLog filter() {
		XLog filteredLog = initializeLog(getLog());
		XEventClassifier classifier = getParameters().getOneFromListClassifier().getSelected().getClassifier();
		Set<String> selectedValues = new HashSet<String>(getParameters().getMultipleFromListString().getSelected());
		for (XTrace trace : getLog()) {
			boolean match = false;
			if (!trace.isEmpty()) {
				String value = classifier.getClassIdentity(trace.get(trace.size() - 1));
				match = selectedValues.contains(value);
			}
			switch (getParameters().getOneFromListSelection().getSelected()) {
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
		return filteredLog;
	}
	
	/*
	 * Make sure the attribute values parameter is initialized.
	 */
	void setAttributeValues(boolean doReset) {
		if (!doReset && getParameters().getMultipleFromListString() != null) {
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
		if (getParameters().getMultipleFromListString() != null) {
			selectedValues.retainAll(getParameters().getMultipleFromListString().getSelected());
		}
		getParameters().setMultipleFromListString(
				new MultipleFromListParameter<String>("Select values", this, selectedValues, unsortedValues, true));
	}

}

