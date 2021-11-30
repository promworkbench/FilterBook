package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.MultipleFromListParameter;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;
import org.processmining.filterbook.types.AttributeType;
import org.processmining.filterbook.types.AttributeValueType;
import org.processmining.filterbook.types.SelectionType;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class EventOccurrencesGlobalAttributeFilter extends Filter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Project on number of occurrences using global attribute value";

	private JComponent attributeValueWidget;

	private XLog cachedLog;
	private XAttribute cachedAttribute;
	private Set<AttributeValueType> cachedSelectedValues;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;

	private Map<String, Integer> occurrences;
	private Map<String, AttributeValueType> occurrenceAttributes;

	public EventOccurrencesGlobalAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
		cachedLog = null;
		occurrences = new HashMap<String, Integer>();
		occurrenceAttributes = new HashMap<String, AttributeValueType>();
	}

	public EventOccurrencesGlobalAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
		cachedLog = null;
		occurrences = new HashMap<String, Integer>();
		occurrenceAttributes = new HashMap<String, AttributeValueType>();
	}

	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasGlobalEventAttributes() && hasEvents();
	}

	private String getEventClass(XEvent event, XAttribute attribute) {
		XAttribute a = event.getAttributes().get(attribute.getKey());
		return (a != null ? a.toString() : "");
	}

	private void setOccurrences() {
		occurrences.clear();
		XAttribute attribute = (getParameters().getOneFromListAttribute().getSelected() != null
				? getParameters().getOneFromListAttribute().getSelected().getAttribute()
				: getDummyAttribute());
		int eventCount = 0;
		for (XTrace trace : getLog()) {
			eventCount += trace.size();
			for (XEvent event : trace) {
				String eventClass = getEventClass(event, attribute);
				if (occurrences.containsKey(eventClass)) {
					occurrences.put(eventClass, occurrences.get(eventClass) + 1);
				} else {
					occurrences.put(eventClass, 1);
				}
			}
		}
		Map<Integer, Integer> eventClasses = new HashMap<Integer, Integer>();
		int m = 1;
		for (int o : occurrences.values()) {
			if (eventClasses.containsKey(o)) {
				eventClasses.put(o, eventClasses.get(o) + 1);
			} else {
				eventClasses.put(o, 1);
			}
			String s = "" + o;
			m = Math.max(m, s.length());
		}
		occurrenceAttributes.clear();
		for (String eventClass : occurrences.keySet()) {
			int o = occurrences.get(eventClass);
			int v = eventClasses.get(o);
			String s = "" + o;
			occurrenceAttributes.put(eventClass,
					new AttributeValueType(getFactory().createAttributeLiteral("", String.format(
							"%" + (2 * m - s.length()) + "d  (%d event class" + (v > 1 ? "es" : "") + ", %.2f %% of log)", o,
							v, (100.0 * o * v / eventCount)), null)));
		}
	}

	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		XAttribute attribute = (getParameters().getOneFromListAttribute().getSelected() != null
				? getParameters().getOneFromListAttribute().getSelected().getAttribute()
				: getDummyAttribute());
		Set<AttributeValueType> selectedValues = new TreeSet<AttributeValueType>(
				getParameters().getMultipleFromListAttributeValue().getSelected());
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
				String eventClass = getEventClass(event, attribute);
				boolean match = selectedValues.contains(occurrenceAttributes.get(eventClass));
				switch (selectionType) {
					case FILTERIN : {
						if (match) {
							filteredTrace.add(event);
						}
						break;
					}
					case FILTEROUT : {
						if (!match) {
							filteredTrace.add(event);
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

	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL },
				{ TableLayoutConstants.FILL, 80 } };
		widget.setLayout(new TableLayout(size));
		widget.add(getParameters().getOneFromListAttribute().getWidget(), "0, 0");
		attributeValueWidget = getParameters().getMultipleFromListAttributeValue().getWidget();
		widget.add(attributeValueWidget, "1, 0");
		widget.add(getParameters().getOneFromListSelection().getWidget(), "0, 1, 1, 1");
		setWidget(widget);
	}

	private void updatedDoInBackground() {
		/*
		 * Reset the attribute values parameter.
		 */
		setAttributeValues(true);
	}

	private void updatedDone() {
		/*
		 * Get the new widget for the attribute value parameter, and replace the old one
		 * with it.
		 */
		getWidget().remove(attributeValueWidget);
		attributeValueWidget = getParameters().getMultipleFromListAttributeValue().getWidget();
		getWidget().add(attributeValueWidget, "1, 0");
		getWidget().revalidate();
		getWidget().repaint();
		getCell().updated();
	}

	public void updated(Parameter parameter) {
		if (parameter == getParameters().getOneFromListAttribute()) {
			getWidget().remove(attributeValueWidget);
			JLabel label = new JLabel("<html><h3>Scanning log, please be patient...</h3></html>");
			label.setHorizontalAlignment(JLabel.CENTER);
			attributeValueWidget = label;
			getWidget().add(attributeValueWidget, "1, 0");
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				public Void doInBackground() {
					updatedDoInBackground();
					return null;
				}

				public void done() {
					updatedDone();
				}
			};
			worker.execute();
			getWidget().revalidate();
			getWidget().repaint();
		}
		getCell().updated();
	}

	void setAttributes(boolean doReset) {
		if (!doReset && getParameters().getOneFromListAttribute() != null) {
			return;
		}
		List<AttributeType> attributes = new ArrayList<AttributeType>();
		for (XAttribute attribute : getLog().getGlobalEventAttributes()) {
			attributes.add(new AttributeType(attribute));
		}
		Collections.sort(attributes);
		AttributeType selectedAttribute = attributes.isEmpty() ? null : attributes.get(0);
		if (getParameters().getOneFromListAttribute() != null
				&& attributes.contains(getParameters().getOneFromListAttribute().getSelected())) {
			selectedAttribute = attributes
					.get(attributes.indexOf(getParameters().getOneFromListAttribute().getSelected()));
		}
		getParameters().setOneFromListAttribute(new OneFromListParameter<AttributeType>("Select a global attribute",
				this, selectedAttribute, attributes, true));
	}

	void setAttributeValues(boolean doReset) {
		if (!doReset && getParameters().getMultipleFromListAttributeValue() != null) {
			return;
		}
		setOccurrences();
		Set<AttributeValueType> values = new TreeSet<AttributeValueType>(occurrenceAttributes.values());
		List<AttributeValueType> unsortedValues = new ArrayList<AttributeValueType>(values);
		List<AttributeValueType> selectedValues = new ArrayList<AttributeValueType>(values);
		if (getParameters().getMultipleFromListAttributeValue() != null) {
			selectedValues.retainAll(getParameters().getMultipleFromListAttributeValue().getSelected());
		}
		getParameters().setMultipleFromListAttributeValue(new MultipleFromListParameter<AttributeValueType>(
				"Select values", this, selectedValues, unsortedValues, true));
	}

	void setSelectionType(boolean doReset) {
		if (!doReset && getParameters().getOneFromListSelection() != null) {
			return;
		}
		SelectionType selected = SelectionType.FILTERIN;
		if (getParameters().getOneFromListSelection() != null) {
			selected = getParameters().getOneFromListSelection().getSelected();
		}
		getParameters().setOneFromListSelection(new OneFromListParameter<SelectionType>("Select a selection type", this,
				selected, Arrays.asList(SelectionType.values()), false));
	}

	public void updateParameters() {
		setAttributes(true);
		setAttributeValues(true);
		setSelectionType(true);
	}

	public FilterTemplate getTemplate() {
		FilterTemplate filterTemplate = new FilterTemplate();
		filterTemplate.setName(getClass().getName());
		filterTemplate.setParameters(new ParametersTemplate());
		filterTemplate.getParameters()
				.setAttribute(getParameters().getOneFromListAttribute().getSelected().getAttribute().getKey());
		filterTemplate.getParameters().setValues(new TreeSet<String>());
		for (AttributeValueType value : getParameters().getMultipleFromListAttributeValue().getSelected()) {
			filterTemplate.getParameters().getValues().add(value.toString());
		}
		filterTemplate.getParameters().setSelection(getParameters().getOneFromListSelection().getSelected().name());
		return filterTemplate;
	}

	public void setTemplate(ParametersTemplate parameters) {
		setAttributes(true);
		if (parameters.getAttribute() != null) {
			for (AttributeType attribute : getParameters().getOneFromListAttribute().getOptions()) {
				if (parameters.getAttribute().equals(attribute.getAttribute().getKey())) {
					getParameters().getOneFromListAttribute().setSelected(attribute);
				}
			}
		}
		setAttributeValues(true);
		if (parameters.getValues() != null) {
			List<AttributeValueType> values = new ArrayList<AttributeValueType>();
			for (AttributeValueType value : getParameters().getMultipleFromListAttributeValue().getOptions()) {
				if (value.getAttribute() != null && parameters.getValues().contains(value.getAttribute().toString())) {
					values.add(value);
				}
			}
			getParameters().getMultipleFromListAttributeValue().setSelected(values);
		}
		setSelectionType(true);
		if (parameters.getSelection() != null) {
			for (SelectionType selection : getParameters().getOneFromListSelection().getOptions()) {
				if (parameters.getSelection().equals(selection.name())) {
					getParameters().getOneFromListSelection().setSelected(selection);
				}
			}
		}
	}
}
