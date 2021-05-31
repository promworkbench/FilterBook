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

public class EventFirstEventAttributeFilter extends EventAttributeFilter {

	public static final String NAME = "Project on first attribute value";

	public EventFirstEventAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public EventFirstEventAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}
	
	public XLog filter() {
		XLog filteredLog = initializeLog(getLog());
		XAttribute attribute = getParameters().getOneFromListAttribute().getSelected().getAttribute();
		Set<AttributeValueType> selectedValues = new TreeSet<AttributeValueType>(getParameters().getMultipleFromListAttributeValue().getSelected());
		for (XTrace trace : getLog()) {
			XTrace filteredTrace = getFactory().createTrace(trace.getAttributes());
			for (XEvent event : trace) {
				AttributeValueType value = new AttributeValueType(event.getAttributes().get(attribute.getKey()));
				boolean match = selectedValues.contains(value);
				switch (getParameters().getOneFromListSelection().getSelected()) {
					case FILTERIN : {
						if (match) {
							if (isFirst(trace, event, attribute)) {
								filteredTrace.add(event);
							}
						} else {
							filteredTrace.add(event);
						}
						break;
					}
					case FILTEROUT : {
						if (match) {
							if (!isFirst(trace, event, attribute)) {
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
	
	private boolean isFirst(XTrace trace, XEvent event, XAttribute attribute) {
		int i = trace.indexOf(event);
		if (i == 0) {
			return true;
		}
		return !attribute.equals(trace.get(i - 1).getAttributes().get(attribute.getKey()));
	}
}
