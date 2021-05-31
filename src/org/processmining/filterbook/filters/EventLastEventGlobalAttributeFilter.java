package org.processmining.filterbook.filters;

import java.util.Set;
import java.util.TreeSet;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeValueType;

public class EventLastEventGlobalAttributeFilter extends EventGlobalAttributeFilter {


	public static final String NAME = "Project on last global attribute value";

	public EventLastEventGlobalAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public EventLastEventGlobalAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	public XLog filter() {
		XLog filteredLog = initializeLog(getLog());
		XAttribute attribute = getParameters().getOneFromListAttribute().getSelected().getAttribute();
		Set<AttributeValueType> selectedValues = new TreeSet<AttributeValueType>(
				getParameters().getMultipleFromListAttributeValue().getSelected());
		for (XTrace trace : getLog()) {
			XTrace filteredTrace = getFactory().createTrace(trace.getAttributes());
			for (XEvent event : trace) {
				AttributeValueType value = new AttributeValueType(event.getAttributes().get(attribute.getKey()));
				boolean match = selectedValues.contains(value);
				switch (getParameters().getOneFromListSelection().getSelected()) {
					case FILTERIN : {
						if (match) {
							if (isLast(trace, event, attribute)) {
								filteredTrace.add(event);
							}
						} else {
							filteredTrace.add(event);
						}
						break;
					}
					case FILTEROUT : {
						if (match) {
							if (!isLast(trace, event, attribute)) {
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

	private boolean isLast(XTrace trace, XEvent event, XAttribute attribute) {
		int i = trace.indexOf(event);
		if (i == trace.size() - 1) {
			return true;
		}
		return !attribute.equals(trace.get(i + 1).getAttributes().get(attribute.getKey()));
	}
}
