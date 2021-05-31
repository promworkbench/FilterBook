package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.MultipleFromListParameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeType;
import org.processmining.filterbook.types.AttributeValueType;

public class TraceLastEventGlobalAttributeFilter extends EventGlobalAttributeFilter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select on last global attribute value";

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
	public TraceLastEventGlobalAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public TraceLastEventGlobalAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	/**
	 * Filter the set log on the start events using the set parameters.
	 */
	public XLog filter() {
		XLog filteredLog = initializeLog(getLog());
		XAttribute attribute = getParameters().getOneFromListAttribute().getSelected().getAttribute();
		Set<AttributeValueType> selectedValues = new TreeSet<AttributeValueType>(getParameters().getMultipleFromListAttributeValue().getSelected());
		for (XTrace trace : getLog()) {
			boolean match = false;
			if (!trace.isEmpty()) {
				AttributeValueType value = new AttributeValueType(trace.get(trace.size() - 1).getAttributes().get(attribute.getKey()));
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
		if (!doReset && getParameters().getMultipleFromListAttributeValue() != null) {
			return;
		}
		AttributeType attribute;
		if (getParameters().getOneFromListAttribute() == null || getParameters().getOneFromListAttribute().getSelected() == null) {
			attribute = new AttributeType(new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, ""));
		} else {
			attribute = getParameters().getOneFromListAttribute().getSelected();
		}
		Set<AttributeValueType> values = new TreeSet<AttributeValueType>();
		for (XTrace trace : getLog()) {
			if (!trace.isEmpty()) {
				values.add(new AttributeValueType(trace.get(trace.size() - 1).getAttributes().get(attribute.getAttribute().getKey())));
			}
		}
		List<AttributeValueType> unsortedValues = new ArrayList<AttributeValueType>(values);
		List<AttributeValueType> selectedValues = new ArrayList<AttributeValueType>(values);
		if (getParameters().getMultipleFromListAttributeValue() != null) {
			selectedValues.retainAll(getParameters().getMultipleFromListAttributeValue().getSelected());
		}
		getParameters().setMultipleFromListAttributeValue(
				new MultipleFromListParameter<AttributeValueType>("Select values", this, selectedValues, unsortedValues, true));
	}

}
