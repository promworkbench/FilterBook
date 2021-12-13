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
import org.processmining.filterbook.types.SelectionType;

public class EventFirstEventAttributeFilter extends EventAttributeFilter {

	public static final String NAME = "Project on first attribute value";

	private XLog cachedLog;
	private XAttribute cachedAttribute;
	private Set<AttributeValueType> cachedSelectedValues;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;

	public EventFirstEventAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
		cachedLog = null;
	}

	public EventFirstEventAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
		cachedLog = null;
	}

	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		XAttribute attribute = (getParameters().getOneFromListAttribute().getSelected() != null
				? getParameters().getOneFromListAttribute().getSelected().getAttribute()
				: getDummyAttribute());
		Set<AttributeValueType> selectedValues = new TreeSet<AttributeValueType>(
				getParameters().getMultipleFromListAttributeValueA().getSelected());
		SelectionType selectionType = getParameters().getOneFromListSelection().getSelected();
		/*
		 * Check whether the cache is valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedAttribute.equals(attribute) && cachedSelectedValues.equals(selectedValues)
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
				AttributeValueType value = new AttributeValueType(event.getAttributes().get(attribute.getKey()));
				boolean match = selectedValues.contains(value);
				switch (selectionType) {
					case FILTERIN : {
						if (match) {
							if (isFirst(trace, event, value.getAttribute())) {
								filteredTrace.add(event);
							}
						} else {
							filteredTrace.add(event);
						}
						break;
					}
					case FILTEROUT : {
						if (match) {
							if (!isFirst(trace, event, value.getAttribute())) {
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
		cachedAttribute = attribute;
		cachedSelectedValues = selectedValues;
		cachedSelectionType = selectionType;
		cachedFilteredLog = filteredLog;
		return filteredLog;
	}

	protected boolean isFirst(XTrace trace, XEvent event, XAttribute attribute) {
		int i = trace.indexOf(event);
		if (i == 0) {
			return true;
		}
		return !attribute.equals(trace.get(i - 1).getAttributes().get(attribute.getKey()));
	}
}
