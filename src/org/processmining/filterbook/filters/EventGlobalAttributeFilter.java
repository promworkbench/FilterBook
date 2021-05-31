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

public class EventGlobalAttributeFilter extends Filter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Project on global attribute value";

	/**
	 * Handle to the widget for attribute values, as this may change if the
	 * classifier parameter is changed.
	 */
	private JComponent attributeValueWidget;

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
	public EventGlobalAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
	}

	public EventGlobalAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
	}

	/**
	 * This filter is suitable if the log contains global event attributes and at least one event.
	 */
	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		if (getLog().getGlobalEventAttributes().isEmpty()) {
			return false;
		}
		for (XTrace trace : getLog()) {
			if (!trace.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Filter the set log on the events using the set parameters.
	 */
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
		return filteredLog;
	}

	/**
	 * Construct a widget for changing the required parameters.
	 */
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
	
	/**
	 * Handle if a parameter values was changed.
	 */
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
			selectedAttribute = attributes.get(attributes.indexOf(getParameters().getOneFromListAttribute().getSelected()));
		}
		getParameters().setOneFromListAttribute(new OneFromListParameter<AttributeType>("Select a global attribute", this,
				selectedAttribute, attributes, true));
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
			for (XEvent event : trace) {
				values.add(new AttributeValueType(event.getAttributes().get(attribute.getAttribute().getKey())));
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
		setAttributeValues(true);
		setSelectionType(true);
	}
	
	public FilterTemplate getTemplate() {
		FilterTemplate filterTemplate = new FilterTemplate();
		filterTemplate.setName(getClass().getName());
		filterTemplate.setParameters(new ParametersTemplate());
		filterTemplate.getParameters().setAttribute(getParameters().getOneFromListAttribute().getSelected().getAttribute().getKey());
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
