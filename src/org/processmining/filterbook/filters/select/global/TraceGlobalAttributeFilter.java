package org.processmining.filterbook.filters.select.global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.charts.TraceChart;
import org.processmining.filterbook.filters.Filter;
import org.processmining.filterbook.filters.FilterTemplate;
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

public class TraceGlobalAttributeFilter extends Filter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select traces";

	/**
	 * Handle to the widget for attribute values, as this may change if the
	 * classifier parameter is changed.
	 */
	private JComponent attributeValueWidget;
	private JComponent chartWidget;

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
	public TraceGlobalAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	public TraceGlobalAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	/**
	 * This filter is suitable if the log contains at least one trace and if it
	 * contains global trace attributes.
	 */
	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasTraces() && hasGlobalTraceAttributes();
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
			AttributeValueType value = new AttributeValueType(trace.getAttributes().get(attribute.getKey()));
			boolean match = selectedValues.contains(value);
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

	/**
	 * Construct a widget for changing the required parameters.
	 */
	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL },
				{ TableLayoutConstants.FILL, TableLayoutConstants.FILL, 80 } };
		widget.setLayout(new TableLayout(size));
		widget.add(getParameters().getOneFromListAttribute().getWidget(), "0, 0");
		attributeValueWidget = getParameters().getMultipleFromListAttributeValueA().getWidget();
		widget.add(attributeValueWidget, "0, 1");
		widget.add(getParameters().getOneFromListSelection().getWidget(), "0, 2");
		chartWidget = getChartWidget();
		widget.add(chartWidget, "1, 0, 1, 2");
		setWidget(widget);
	}

	public JComponent getChartWidget() {
		return TraceChart.getChart(getLog(),  getParameters());
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
		attributeValueWidget = getParameters().getMultipleFromListAttributeValueA().getWidget();
		getWidget().add(attributeValueWidget, "0, 1");
		getWidget().remove(chartWidget);
		chartWidget = getChartWidget();
		getWidget().add(chartWidget, "1, 0, 1, 2");
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
	public void setAttributes(boolean doReset) {
		if (!doReset && getParameters().getOneFromListAttribute() != null) {
			return;
		}
		List<AttributeType> attributes = new ArrayList<AttributeType>();
		for (XAttribute attribute : getLog().getGlobalTraceAttributes()) {
			attributes.add(new AttributeType(attribute));
		}
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
	public void setAttributeValues(boolean doReset) {
		if (!doReset && getParameters().getMultipleFromListAttributeValueA() != null) {
			System.out.println("TraceGlobalAttributeFilter] Done");
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
			values.add(new AttributeValueType(trace.getAttributes().get(attribute.getAttribute().getKey())));
		}
		List<AttributeValueType> unsortedValues = new ArrayList<AttributeValueType>(values);
		List<AttributeValueType> selectedValues = new ArrayList<AttributeValueType>(values);
		if (getParameters().getMultipleFromListAttributeValueA() != null) {
			selectedValues.retainAll(getParameters().getMultipleFromListAttributeValueA().getSelected());
		}
		getParameters().setMultipleFromListAttributeValueA(new MultipleFromListParameter<AttributeValueType>(
				"Select values", this, selectedValues, unsortedValues, true));
	}

	/*
	 * Make sure the selection type parameter is initialized.
	 */
	public void setSelectionType(boolean doReset) {
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
		filterTemplate.getParameters().setValuesA(new TreeSet<String>());
		for (AttributeValueType value : getParameters().getMultipleFromListAttributeValueA().getSelected()) {
			filterTemplate.getParameters().getValuesA().add(value.toString());
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
		if (parameters.getValuesA() != null) {
			List<AttributeValueType> values = new ArrayList<AttributeValueType>();
			for (AttributeValueType value : getParameters().getMultipleFromListAttributeValueA().getOptions()) {
				if (value.getAttribute() != null && parameters.getValuesA().contains(value.getAttribute().toString())) {
					values.add(value);
				}
			}
			getParameters().getMultipleFromListAttributeValueA().setSelected(values);
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
