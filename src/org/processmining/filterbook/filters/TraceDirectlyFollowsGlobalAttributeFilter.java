package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
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

public class TraceDirectlyFollowsGlobalAttributeFilter extends Filter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select on two directly-following global attribute values";

	/**
	 * Handle to the widget for attribute values, as this may change if the
	 * classifier parameter is changed.
	 */
	private JComponent attributeValueWidgetA;
	private JComponent attributeValueWidgetB;

	private XLog cachedLog;
	private XAttribute cachedAttribute;
	private Set<AttributeValueType> cachedSelectedValuesA;
	private Set<AttributeValueType> cachedSelectedValuesB;
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
	public TraceDirectlyFollowsGlobalAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	public TraceDirectlyFollowsGlobalAttributeFilter(String name, XLog log, Parameters parameters,
			ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	/**
	 * This filter is suitable if the log contains global event attributes and at
	 * least one event.
	 */
	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasGlobalEventAttributes() && hasEvents();
	}

	/**
	 * Filter the set log on the events using the set parameters.
	 */
	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		XAttribute attribute = (getParameters().getOneFromListAttribute().getSelected() != null
				? getParameters().getOneFromListAttribute().getSelected().getAttribute()
				: getDummyAttribute());
		Set<AttributeValueType> selectedValuesA = new TreeSet<AttributeValueType>(
				getParameters().getMultipleFromListAttributeValueA().getSelected());
		Set<AttributeValueType> selectedValuesB = new TreeSet<AttributeValueType>(
				getParameters().getMultipleFromListAttributeValueB().getSelected());
		SelectionType selectionType = getParameters().getOneFromListSelection().getSelected();
		/*
		 * Check whether the cache is valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedAttribute.equals(attribute) && cachedSelectedValuesA.equals(selectedValuesA)
					&& cachedSelectedValuesB.equals(selectedValuesB) && cachedSelectionType == selectionType) {
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
			boolean match = checkTrace(trace, attribute, selectedValuesA, selectedValuesB);
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
		cachedSelectedValuesA = selectedValuesA;
		cachedSelectedValuesB = selectedValuesB;
		cachedSelectionType = selectionType;
		cachedFilteredLog = filteredLog;
		return filteredLog;
	}

	protected boolean checkTrace(XTrace trace, XAttribute attribute, Set<AttributeValueType> selectedValuesA,
			Set<AttributeValueType> selectedValuesB) {
		for (int i = 0; i < trace.size() - 2; i++) {
			XAttribute attributeA = trace.get(i).getAttributes().get(attribute.getKey());
			XAttribute attributeB = trace.get(i + 1).getAttributes().get(attribute.getKey());
			if (attributeA != null && attributeB != null && selectedValuesA.contains(new AttributeValueType(attributeA))
					&& selectedValuesB.contains(new AttributeValueType(attributeB))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Construct a widget for changing the required parameters.
	 */
	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL },
				{ TableLayoutConstants.FILL, TableLayoutConstants.FILL, 80 } };
		widget.setLayout(new TableLayout(size));
		widget.add(getParameters().getOneFromListAttribute().getWidget(), "0, 0, 0, 1");
		attributeValueWidgetA = getParameters().getMultipleFromListAttributeValueA().getWidget();
		widget.add(attributeValueWidgetA, "1, 0");
		attributeValueWidgetB = getParameters().getMultipleFromListAttributeValueB().getWidget();
		widget.add(attributeValueWidgetB, "1, 1");
		widget.add(getParameters().getOneFromListSelection().getWidget(), "0, 2, 1, 2");
		setWidget(widget);
	}

	private void updatedDoInBackground() {
		/*
		 * Reset the attribute values parameter.
		 */
		setAttributeValuesA(true);
		setAttributeValuesB(true);
	}

	private void updatedDone() {
		/*
		 * Get the new widget for the attribute value parameter, and replace the old one
		 * with it.
		 */
		getWidget().remove(attributeValueWidgetA);
		attributeValueWidgetA = getParameters().getMultipleFromListAttributeValueA().getWidget();
		getWidget().add(attributeValueWidgetA, "1, 0");
		getWidget().remove(attributeValueWidgetB);
		attributeValueWidgetB = getParameters().getMultipleFromListAttributeValueB().getWidget();
		getWidget().add(attributeValueWidgetB, "1, 1");
		getWidget().revalidate();
		getWidget().repaint();
		getCell().updated();
	}

	/**
	 * Handle if a parameter values was changed.
	 */
	public void updated(Parameter parameter) {
		if (parameter == getParameters().getOneFromListAttribute()) {
			getWidget().remove(attributeValueWidgetA);
			getWidget().remove(attributeValueWidgetB);
			JLabel label = new JLabel("<html><h3>Scanning log, please be patient...</h3></html>");
			label.setHorizontalAlignment(JLabel.CENTER);
			attributeValueWidgetA = label;
			getWidget().add(attributeValueWidgetA, "1, 0, 1, 1");
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

	/*
	 * Make sure the classifier parameter is initialized.
	 */
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

	/*
	 * Make sure the attribute values parameter is initialized.
	 */
	void setAttributeValuesA(boolean doReset) {
		if (!doReset && getParameters().getMultipleFromListAttributeValueA() != null) {
			return;
		}
		AttributeType attribute;
		if (getParameters().getOneFromListAttribute() == null
				|| getParameters().getOneFromListAttribute().getSelected() == null) {
			attribute = new AttributeType(new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, ""));
		} else {
			attribute = getParameters().getOneFromListAttribute().getSelected();
		}
		Set<AttributeValueType> values = new TreeSet<AttributeValueType>();
		for (XTrace trace : getLog()) {
			for (XEvent event : trace) {
				XAttribute attr = event.getAttributes().get(attribute.getAttribute().getKey());
				if (attr != null) {
					values.add(new AttributeValueType(attr));
				}
			}
		}
		List<AttributeValueType> unsortedValues = new ArrayList<AttributeValueType>(values);
		List<AttributeValueType> selectedValues = new ArrayList<AttributeValueType>(values);
		if (getParameters().getMultipleFromListAttributeValueA() != null) {
			selectedValues.retainAll(getParameters().getMultipleFromListAttributeValueA().getSelected());
		}
		getParameters().setMultipleFromListAttributeValueA(new MultipleFromListParameter<AttributeValueType>(
				"Select A values", this, selectedValues, unsortedValues, true));
	}

	/*
	 * Make sure the attribute values parameter is initialized.
	 */
	void setAttributeValuesB(boolean doReset) {
		if (!doReset && getParameters().getMultipleFromListAttributeValueB() != null) {
			return;
		}
		AttributeType attribute;
		if (getParameters().getOneFromListAttribute() == null
				|| getParameters().getOneFromListAttribute().getSelected() == null) {
			attribute = new AttributeType(new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, ""));
		} else {
			attribute = getParameters().getOneFromListAttribute().getSelected();
		}
		Set<AttributeValueType> values = new TreeSet<AttributeValueType>();
		for (XTrace trace : getLog()) {
			for (XEvent event : trace) {
				XAttribute attr = event.getAttributes().get(attribute.getAttribute().getKey());
				if (attr != null) {
					values.add(new AttributeValueType(attr));
				}
			}
		}
		List<AttributeValueType> unsortedValues = new ArrayList<AttributeValueType>(values);
		List<AttributeValueType> selectedValues = new ArrayList<AttributeValueType>(values);
		if (getParameters().getMultipleFromListAttributeValueB() != null) {
			selectedValues.retainAll(getParameters().getMultipleFromListAttributeValueB().getSelected());
		}
		getParameters().setMultipleFromListAttributeValueB(new MultipleFromListParameter<AttributeValueType>(
				"Select B values", this, selectedValues, unsortedValues, true));
	}

	/*
	 * Make sure the selection type parameter is initialized.
	 */
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
		setAttributeValuesA(true);
		setAttributeValuesB(true);
		setSelectionType(true);
	}

	public FilterTemplate getTemplate() {
		FilterTemplate filterTemplate = new FilterTemplate();
		filterTemplate.setName(getClass().getName());
		filterTemplate.setParameters(new ParametersTemplate());
		filterTemplate.getParameters()
				.setAttribute(getParameters().getOneFromListAttribute().getSelected().getAttribute().getKey());
		filterTemplate.getParameters().setValuesA(new TreeSet<String>());
		for (AttributeValueType value : getParameters().getMultipleFromListAttributeValueA().getSelected()) {
			filterTemplate.getParameters().getValuesA().add(value.toString());
		}
		filterTemplate.getParameters().setValuesB(new TreeSet<String>());
		for (AttributeValueType value : getParameters().getMultipleFromListAttributeValueB().getSelected()) {
			filterTemplate.getParameters().getValuesB().add(value.toString());
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
		setAttributeValuesA(true);
		if (parameters.getValuesA() != null) {
			List<AttributeValueType> values = new ArrayList<AttributeValueType>();
			for (AttributeValueType value : getParameters().getMultipleFromListAttributeValueA().getOptions()) {
				if (value.getAttribute() != null && parameters.getValuesA().contains(value.getAttribute().toString())) {
					values.add(value);
				}
			}
			getParameters().getMultipleFromListAttributeValueA().setSelected(values);
		}
		setAttributeValuesB(true);
		if (parameters.getValuesB() != null) {
			List<AttributeValueType> values = new ArrayList<AttributeValueType>();
			for (AttributeValueType value : getParameters().getMultipleFromListAttributeValueB().getOptions()) {
				if (value.getAttribute() != null && parameters.getValuesB().contains(value.getAttribute().toString())) {
					values.add(value);
				}
			}
			getParameters().getMultipleFromListAttributeValueB().setSelected(values);
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
