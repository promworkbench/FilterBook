package org.processmining.filterbook.filters.select.global;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.charts.FirstChart;
import org.processmining.filterbook.filters.project.global.EventGlobalAttributeFilter;
import org.processmining.filterbook.parameters.MultipleFromListParameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeType;
import org.processmining.filterbook.types.AttributeValueType;
import org.processmining.filterbook.types.SelectionType;

public class TraceFirstEventGlobalAttributeFilter extends EventGlobalAttributeFilter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select traces on first event";

	private XLog cachedLog;
	private XAttribute cachedAttribute;
	private Set<AttributeValueType> cachedSelectedValues;
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
	public TraceFirstEventGlobalAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
		cachedLog = null;
	}

	public TraceFirstEventGlobalAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
		cachedLog = null;
	}

	/**
	 * Filter the set log on the start events using the set parameters.
	 */
	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		XAttribute attribute = (getParameters().getOneFromListAttribute().getSelected() != null
				? getParameters().getOneFromListAttribute().getSelected().getAttribute()
				: getDummyAttribute());
		Set<AttributeValueType> selectedValues = new TreeSet<AttributeValueType>(getParameters().getMultipleFromListAttributeValueA().getSelected());
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
			boolean match = false;
			if (!trace.isEmpty()) {
				AttributeValueType value = new AttributeValueType(trace.get(0).getAttributes().get(attribute.getKey()));
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
		cachedAttribute = attribute;
		cachedSelectedValues = selectedValues;
		cachedSelectionType = selectionType;
		cachedFilteredLog = filteredLog;
		return filteredLog;
	}

	public JComponent getChartWidget() {
		return FirstChart.getChart(getLog(), getParameters());
	}

	/*
	 * Make sure the attribute values parameter is initialized.
	 */
	public void setAttributeValues(boolean doReset) {
		if (!doReset && getParameters().getMultipleFromListAttributeValueA() != null) {
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
				values.add(new AttributeValueType(trace.get(0).getAttributes().get(attribute.getAttribute().getKey())));
			}
		}
		List<AttributeValueType> unsortedValues = new ArrayList<AttributeValueType>(values);
		List<AttributeValueType> selectedValues = new ArrayList<AttributeValueType>(values);
		if (getParameters().getMultipleFromListAttributeValueA() != null) {
			selectedValues.retainAll(getParameters().getMultipleFromListAttributeValueA().getSelected());
		}
		getParameters().setMultipleFromListAttributeValueA(
				new MultipleFromListParameter<AttributeValueType>("Select values", this, selectedValues, unsortedValues, true));
	}

}
