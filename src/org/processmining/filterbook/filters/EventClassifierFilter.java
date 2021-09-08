package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.MultipleFromListParameter;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;
import org.processmining.filterbook.types.ClassifierType;
import org.processmining.filterbook.types.SelectionType;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class EventClassifierFilter extends Filter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Project on classifier value";

	/**
	 * Handle to the widget for attribute values, as this may change if the
	 * classifier parameter is changed.
	 */
	private JComponent attributeValueWidget;

	/**
	 * Caching filter results to avoid filtering the same log with the same settings over and over again.
	 */
	private XLog cachedLog;
	private XEventClassifier cachedClassifier;
	private Set<String> cachedSelectedValues;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;
	
	public EventClassifierFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		cachedLog = null;
		setLog(log);
	}

	public EventClassifierFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		cachedLog = null;
		setLog(log);
	}

	/**
	 * This filter is suitable if the log contains classifiers and at least one event.
	 */
	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasClassifiers() && hasEvents();
	}

	/**
	 * Filter the set log on the events using the set parameters.
	 */
	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		XEventClassifier classifier = getParameters().getOneFromListClassifier().getSelected().getClassifier();
		Set<String> selectedValues = new HashSet<String>(getParameters().getMultipleFromListString().getSelected());
		SelectionType selectionType = getParameters().getOneFromListSelection().getSelected();
		/*
		 * Check whether the cache is  valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedClassifier.equals(classifier) &&
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
				String value = classifier.getClassIdentity(event);
				boolean match = selectedValues.contains(value);
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
				{ TableLayoutConstants.FILL, 80 } };
		widget.setLayout(new TableLayout(size));
		widget.add(getParameters().getOneFromListClassifier().getWidget(), "0, 0");
		attributeValueWidget = getParameters().getMultipleFromListString().getWidget();
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
		attributeValueWidget = getParameters().getMultipleFromListString().getWidget();
		getWidget().add(attributeValueWidget, "1, 0");
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
		if (!doReset && getParameters().getMultipleFromListString() != null) {
			return;
		}
		XEventClassifier classifier;
		if (getParameters().getOneFromListClassifier() == null
				|| getParameters().getOneFromListClassifier().getSelected() == null) {
			classifier = new XEventNameClassifier();
		} else {
			classifier = getParameters().getOneFromListClassifier().getSelected().getClassifier();
		}
		Set<String> values = new HashSet<String>();
		for (XTrace trace : getLog()) {
			for (XEvent event : trace) {
				values.add(classifier.getClassIdentity(event));
			}
		}
		List<String> unsortedValues = new ArrayList<String>(values);
		List<String> selectedValues = new ArrayList<String>(values);
		if (getParameters().getMultipleFromListString() != null) {
			selectedValues.retainAll(getParameters().getMultipleFromListString().getSelected());
		}
		getParameters().setMultipleFromListString(
				new MultipleFromListParameter<String>("Select values", this, selectedValues, unsortedValues, true));
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
		filterTemplate.getParameters().setClassifier(getParameters().getOneFromListClassifier().getSelected().getClassifier().name());
		// Copy the selected classifier values.
		filterTemplate.getParameters().setValues(new TreeSet<String>());
		for (String value : getParameters().getMultipleFromListString().getSelected()) {
			filterTemplate.getParameters().getValues().add(value);
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
		if (parameters.getValues() != null) {
			List<String> values = new ArrayList<String>();
			for (String value : getParameters().getMultipleFromListString().getOptions()) {
				if (value != null && parameters.getValues().contains(value)) {
					values.add(value);
				}
			}
			getParameters().getMultipleFromListString().setSelected(values);
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
