package org.processmining.filterbook.filters.project.attribute;

import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.charts.DirectlyFollowsChart;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeValueType;
import org.processmining.filterbook.types.SelectionType;

public class EventLastEventAttributeFilter extends EventAttributeFilter {

	public static final String NAME = "Project series on last event";

	private XLog cachedLog;
	private XAttribute cachedAttribute;
	private Set<AttributeValueType> cachedSelectedValues;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;

	public EventLastEventAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
		cachedLog = null;
	}

	public EventLastEventAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
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
		 * Check whether the cache is  valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedAttribute.equals(attribute) &&
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
			XTrace filteredTrace = getFactory().createTrace(trace.getAttributes());
			for (XEvent event : trace) {
				AttributeValueType value = new AttributeValueType(event.getAttributes().get(attribute.getKey()));
				boolean match = selectedValues.contains(value);
				switch (selectionType) {
					case FILTERIN : {
						if (match) {
							if (isLast(trace, event, value.getAttribute())) {
								filteredTrace.add(event);
							}
						} else {
							filteredTrace.add(event);
						}
						break;
					}
					case FILTEROUT : {
						if (match) {
							if (!isLast(trace, event, value.getAttribute())) {
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

	public boolean isLast(XTrace trace, XEvent event, XAttribute attribute) {
		int i = trace.indexOf(event);
		if (i == trace.size() - 1) {
			return true;
		}
		return !attribute.equals(trace.get(i + 1).getAttributes().get(attribute.getKey()));
	}
	
	public JComponent getChartWidget() {
		return DirectlyFollowsChart.getChart(getLog(), getParameters());
	}

}
