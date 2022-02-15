package org.processmining.filterbook.filters.select.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.charts.EventsChart;
import org.processmining.filterbook.filters.Filter;
import org.processmining.filterbook.filters.FilterTemplate;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;
import org.processmining.filterbook.types.ClassifierType;
import org.processmining.filterbook.types.SelectionType;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public abstract class TraceVariantAbstractClassifierFilter extends Filter {


	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select one trace for every variant";

	/**
	 * Handle to the widget for chart, as this may change if the
	 * classifier parameter is changed.
	 */
	private JComponent chartWidget;

	/**
	 * Caching filter results to avoid filtering the same log with the same settings
	 * over and over again.
	 */
	private XLog cachedLog;
	private XEventClassifier cachedClassifier;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;

	protected Map<List<String>, List<XTrace>> traces;
	protected Map<List<String>, List<XTrace>> selectedTraces;

	public TraceVariantAbstractClassifierFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		cachedLog = null;
		setLog(log);
		traces = new HashMap<List<String>, List<XTrace>>();
		selectedTraces = null;
	}

	public TraceVariantAbstractClassifierFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		cachedLog = null;
		setLog(log);
		traces = new HashMap<List<String>, List<XTrace>>();
		selectedTraces = null;
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
	private List<String> getTraceClass(XTrace trace, XEventClassifier classifier) {
		List<String> values = new ArrayList<String>();
		for (XEvent event : trace) {
			String value = classifier.getClassIdentity(event);
			values.add(value);
		}
		return values;
	}

	/*
	 * Refresh the occurrences.
	 */
	private void setTraces() {
		traces.clear();
		selectedTraces = null;
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
		for (XTrace trace : getLog()) {
			List<String> traceClass = getTraceClass(trace, classifier);
			if (traces.containsKey(traceClass)) {
				traces.get(traceClass).add(trace);
			} else {
				List<XTrace> traceList = new ArrayList<XTrace>();
				traceList.add(trace);
				traces.put(traceClass, traceList);
			}
		}
	}

	public abstract void select();
	
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
		SelectionType selectionType = getParameters().getOneFromListSelection().getSelected();
		/*
		 * Check whether the cache is valid.
		 */		
		if (cachedLog == getLog()) {
			if (cachedClassifier.equals(classifier) && cachedSelectionType == selectionType) {
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
		/*
		 * Make sure the traces are up-to-date.
		 */
		setTraces();
		/*
		 * Select the appropriate traces.
		 */
		select();
		for (XTrace trace : getLog()) {
			/*
			 * Construct the variant for this trace, given the classifier.
			 */
			List<String> traceClass = getTraceClass(trace, classifier);
			/*
			 * Check whether this variant has been selected.
			 */
			boolean match = selectedTraces.get(traceClass).contains(trace);
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
		cachedClassifier = classifier;
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
		widget.add(getParameters().getOneFromListClassifier().getWidget(), "0, 0, 0, 1");
		widget.add(getParameters().getOneFromListSelection().getWidget(), "0, 2");
		chartWidget = getChartWidget();
		widget.add(chartWidget, "1, 0, 1, 2");
		setWidget(widget);
	}

	public JComponent getChartWidget() {
		Map<List<String>, Integer> occurrences = new HashMap<List<String>, Integer>();
		setTraces();
		for (List<String> traceClass : traces.keySet()) {
			occurrences.put(traceClass, traces.get(traceClass).size());
		}
		return EventsChart.getChart(occurrences, getDummyClassifier(), getParameters());
	}

	private void updatedDoInBackground() {
//		setTraces();
	}

	private void updatedDone() {
		/*
		 * Get the new widget for the attribute value parameter, and replace the old one
		 * with it.
		 */
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
			getWidget().remove(chartWidget);
			JLabel label = new JLabel("<html><h3>Scanning log, please be patient...</h3></html>");
			label.setHorizontalAlignment(JLabel.CENTER);
			chartWidget = label;
			getWidget().add(chartWidget, "1, 0, ,1, 2");
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
	public void setClassifiers(boolean doReset) {
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

	/**
	 * Update all parameters.
	 */
	public void updateParameters() {
		// Update the classifier.
		setClassifiers(true);
//		setTraces();
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
