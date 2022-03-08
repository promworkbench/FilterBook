package org.processmining.filterbook.filters.select.global;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.charts.DurationChart;
import org.processmining.filterbook.filters.Filter;
import org.processmining.filterbook.filters.FilterTemplate;
import org.processmining.filterbook.parameters.MultipleFromListParameter;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;
import org.processmining.filterbook.types.DurationType;
import org.processmining.filterbook.types.SelectionType;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TraceDurationFilter extends Filter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select traces on duration";

	private JComponent traceDurationWidget;

	private XLog cachedLog;
	private Set<DurationType> cachedSelectedDurations;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;

	public TraceDurationFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	public TraceDurationFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	/**
	 * This filter is suitable if the log contains at least one trace.
	 */
	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasTimeExtension() && hasGlobalTimestamp() && hasEvents();
	}

	/**
	 * Filter the set log on the start events using the set parameters.
	 */
	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		Set<DurationType> selectedDurations = new HashSet<DurationType>(
				getParameters().getMultipleFromListDuration().getSelected());
		SelectionType selectionType = getParameters().getOneFromListSelection().getSelected();
		/*
		 * Check whether the cache is valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedSelectedDurations.equals(selectedDurations) && cachedSelectionType == selectionType) {
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
			DurationType duration = new DurationType(
					Duration.between(XTimeExtension.instance().extractTimestamp(trace.get(0)).toInstant(),
							XTimeExtension.instance().extractTimestamp(trace.get(trace.size() - 1)).toInstant()));
			boolean match = selectedDurations.contains(duration);
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
		cachedSelectedDurations = selectedDurations;
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
		traceDurationWidget = getParameters().getMultipleFromListDuration().getWidget();
		widget.add(traceDurationWidget, "0, 0");
		widget.add(getParameters().getOneFromListSelection().getWidget(), "0, 1");

		widget.add(getChartWidget(), "1, 0, 1, 1");

		setWidget(widget);
	}

	private JComponent getChartWidget() {
		return DurationChart.getChart(getLog());
	}

	/**
	 * Handle if a parameter values was changed.
	 */
	public void updated(Parameter parameter) {
		getCell().updated();
	}

	private void setTraceDurations(boolean doReset) {
		if (!doReset && getParameters().getMultipleFromListDuration() != null) {
			return;
		}
		Set<DurationType> traceDurations = new HashSet<DurationType>();
		for (XTrace trace : getLog()) {
			DurationType duration = new DurationType(
					Duration.between(XTimeExtension.instance().extractTimestamp(trace.get(0)).toInstant(),
							XTimeExtension.instance().extractTimestamp(trace.get(trace.size() - 1)).toInstant()));
			traceDurations.add(duration);
		}
		List<DurationType> unsortedDurations = new ArrayList<DurationType>(traceDurations);
		List<DurationType> selectedDurations = new ArrayList<DurationType>(traceDurations);
		if (getParameters().getMultipleFromListDuration() != null) {
			selectedDurations.retainAll(getParameters().getMultipleFromListDuration().getSelected());
		}
		getParameters().setMultipleFromListDuration(new MultipleFromListParameter<DurationType>(
				"Select trace durations", this, selectedDurations, unsortedDurations, true));
	}

	/*
	 * Make sure the selection type parameter is initialized.
	 */
	private void setSelectionType(boolean doReset) {
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
	 * Update the filter parameters.
	 */
	public void updateParameters() {
		setTraceDurations(true);
		setSelectionType(true);
	}

	public FilterTemplate getTemplate() {
		FilterTemplate filterTemplate = new FilterTemplate();
		filterTemplate.setName(getClass().getName());
		filterTemplate.setParameters(new ParametersTemplate());
		filterTemplate.getParameters().setValuesA(new TreeSet<String>());
		for (DurationType selected : getParameters().getMultipleFromListDuration().getSelected()) {
			filterTemplate.getParameters().getValuesA().add(selected.toString());
		}
		filterTemplate.getParameters().setSelection(getParameters().getOneFromListSelection().getSelected().name());
		return filterTemplate;
	}

	public void setTemplate(ParametersTemplate parameters) {
		setTraceDurations(true);
		if (parameters.getValuesA() != null) {
			List<DurationType> values = new ArrayList<DurationType>();
			for (DurationType value : getParameters().getMultipleFromListDuration().getOptions()) {
				if (value != null && parameters.getValuesA().contains(value.toString())) {
					values.add(value);
				}
			}
			getParameters().getMultipleFromListDuration().setSelected(values);
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
