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

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.charts.EventChart;
import org.processmining.filterbook.parameters.MultipleFromListParameter;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;
import org.processmining.filterbook.types.AttributeValueType;
import org.processmining.filterbook.types.ClassifierType;
import org.processmining.filterbook.types.SelectionType;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class EventOccurrencesClassifierFilter extends Filter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Project on number of occurrences in log";

	/**
	 * Handle to the widget for attribute values, as this may change if the
	 * classifier parameter is changed.
	 */
	private JComponent attributeValueWidget;
	private JComponent chartWidget;

	/**
	 * Caching filter results to avoid filtering the same log with the same settings
	 * over and over again.
	 */
	private XLog cachedLog;
	private XEventClassifier cachedClassifier;
	private Set<AttributeValueType> cachedSelectedValues;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;

	private Map<String, Integer> occurrences;
	private Map<String, AttributeValueType> occurrenceAttributes;

	public EventOccurrencesClassifierFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		cachedLog = null;
		setLog(log);
		occurrences = new HashMap<String, Integer>();
		occurrenceAttributes = new HashMap<String, AttributeValueType>();
	}

	public EventOccurrencesClassifierFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		cachedLog = null;
		setLog(log);
		occurrences = new HashMap<String, Integer>();
		occurrenceAttributes = new HashMap<String, AttributeValueType>();
	}

	/**
	 * This filter is suitable if the log contains classifiers and at least one
	 * event.
	 */
	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasClassifiers() && hasEvents();
	}

	/*
	 * Returns the variant for this trace, given the classifier to use.
	 */
	private String getEventClass(XEvent event, XEventClassifier classifier) {
		return classifier.getClassIdentity(event);
	}

	/*
	 * Refresh the occurrences.
	 */
	private void setOccurrences() {
		occurrences.clear();
		occurrenceAttributes.clear();
		if (!isSuitable()) {
			return;
		}
		/*
		 * Get the classifier to use.
		 */
		XEventClassifier classifier = getParameters().getOneFromListClassifier().getSelected().getClassifier();
		/*
		 * Compute the occurrences for all variants.
		 */
		int eventCount = 0;
		for (XTrace trace : getLog()) {
			eventCount += trace.size();
			for (XEvent event : trace) {
				String eventClass = getEventClass(event, classifier);
				if (occurrences.containsKey(eventClass)) {
					occurrences.put(eventClass, occurrences.get(eventClass) + 1);
				} else {
					occurrences.put(eventClass, 1);
				}
			}
		}
		/*
		 * Count how many variants there are for every occurrence.
		 */
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
		/*
		 * Construct the list of options to choose from. Every option lists the number
		 * of occurrences, the number of variants for this number of occurrences, and
		 * the percentage of traces this option covers.
		 */
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

	/**
	 * Filter the set log on the events using the set parameters.
	 */
	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		XEventClassifier classifier = (getParameters().getOneFromListClassifier().getSelected() != null
				? getParameters().getOneFromListClassifier().getSelected().getClassifier()
				: getDummyClassifier());
		Set<AttributeValueType> selectedValues = new TreeSet<AttributeValueType>(
				getParameters().getMultipleFromListAttributeValueA().getSelected());
		SelectionType selectionType = getParameters().getOneFromListSelection().getSelected();
		/*
		 * Check whether the cache is valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedClassifier.equals(classifier) && cachedSelectedValues.equals(selectedValues)
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
				String eventClass = getEventClass(event, classifier);
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
		cachedClassifier = classifier;
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
		widget.add(getParameters().getOneFromListClassifier().getWidget(), "0, 0");
		attributeValueWidget = getParameters().getMultipleFromListAttributeValueA().getWidget();
		widget.add(attributeValueWidget, "0, 1");
		widget.add(getParameters().getOneFromListSelection().getWidget(), "0, 2");
		chartWidget = getChartWidget();
		widget.add(chartWidget, "1, 0, 1, 2");
		setWidget(widget);
	}

	protected JComponent getChartWidget() {
		return EventChart.getChart(occurrences, getDummyClassifier(), getParameters());
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
	 * Handle if a parameter was changed.
	 */
	public void updated(Parameter parameter) {
		if (parameter == getParameters().getOneFromListClassifier()) {
			// Classifier parameter changed. Get new classifier values from the log.
			getWidget().remove(attributeValueWidget);
			JLabel label = new JLabel("<html><h3>Scanning log, please be patient...</h3></html>");
			label.setHorizontalAlignment(JLabel.CENTER);
			attributeValueWidget = label;
			getWidget().add(attributeValueWidget, "1, 0");
			getWidget().revalidate();
			getWidget().repaint();
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				public Void doInBackground() {
					// Get new classifier values.
					updatedDoInBackground();
					return null;
				}

				public void done() {
					// Get widget for new classifier values.
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
	void setClassifiers(boolean doReset) {
		if (!doReset && getParameters().getOneFromListClassifier() != null) {
			return;
		}
		List<ClassifierType> classifiers = new ArrayList<ClassifierType>();
		for (XEventClassifier classifier : getLog().getClassifiers()) {
			classifiers.add(new ClassifierType(classifier));
		}
		Collections.sort(classifiers);
		ClassifierType selectedClassifier = classifiers.isEmpty() ? null : classifiers.get(0);
		if (getParameters().getOneFromListClassifier() != null
				&& classifiers.contains(getParameters().getOneFromListClassifier().getSelected())) {
			selectedClassifier = classifiers
					.get(classifiers.indexOf(getParameters().getOneFromListClassifier().getSelected()));
		}
		getParameters().setOneFromListClassifier(new OneFromListParameter<ClassifierType>("Select a classifier", this,
				selectedClassifier, classifiers, true));
	}

	/*
	 * Make sure the attribute values parameter is initialized.
	 */
	void setAttributeValues(boolean doReset) {
		if (!doReset && getParameters().getMultipleFromListAttributeValueA() != null) {
			return;
		}
		setOccurrences();
		Set<AttributeValueType> values = new TreeSet<AttributeValueType>(occurrenceAttributes.values());
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

	/**
	 * Update all parameters.
	 */
	public void updateParameters() {
		// Update the classifier.
		setClassifiers(true);
		// Update the classifier values.
		setAttributeValues(true);
		// Update the selection type.
		setSelectionType(true);
	}

	/**
	 * Gets a template for this filter.
	 */
	public FilterTemplate getTemplate() {
		// Create a fresh template.
		FilterTemplate filterTemplate = new FilterTemplate();
		// Copy the filter name.
		filterTemplate.setName(getClass().getName());
		// Create a new parameters template.
		filterTemplate.setParameters(new ParametersTemplate());
		// Copy the selected classifier.
		filterTemplate.getParameters()
				.setClassifier(getParameters().getOneFromListClassifier().getSelected().getClassifier().name());
		// Copy the selected classifier values.
		filterTemplate.getParameters().setValuesA(new TreeSet<String>());
		for (AttributeValueType value : getParameters().getMultipleFromListAttributeValueA().getSelected()) {
			filterTemplate.getParameters().getValuesA().add(value.toString());
		}
		// Copy the selection type.
		filterTemplate.getParameters().setSelection(getParameters().getOneFromListSelection().getSelected().name());
		return filterTemplate;
	}

	/**
	 * Initializes a filter from the given parameters template.
	 */
	public void setTemplate(ParametersTemplate parameters) {
		// Initialize the classifier.
		setClassifiers(true);
		// Select the classifier.
		if (parameters.getClassifier() != null) {
			for (ClassifierType classifier : getParameters().getOneFromListClassifier().getOptions()) {
				if (parameters.getClassifier().equals(classifier.getClassifier().name())) {
					getParameters().getOneFromListClassifier().setSelected(classifier);
				}
			}
		}
		// Initialize the classifier values.
		setAttributeValues(true);
		// Select the classifier values.
		if (parameters.getValuesA() != null) {
			List<String> values = new ArrayList<String>();
			for (String value : getParameters().getMultipleFromListStringA().getOptions()) {
				if (value != null && parameters.getValuesA().contains(value)) {
					values.add(value);
				}
			}
			getParameters().getMultipleFromListStringA().setSelected(values);
		}
		// Initialize the selection type.
		setSelectionType(true);
		// Select the selection type.
		if (parameters.getSelection() != null) {
			for (SelectionType selection : getParameters().getOneFromListSelection().getOptions()) {
				if (parameters.getSelection().equals(selection.name())) {
					getParameters().getOneFromListSelection().setSelected(selection);
				}
			}
		}
	}
}
