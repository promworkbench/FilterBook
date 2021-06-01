package org.processmining.filterbook.filters;

import javax.swing.JComponent;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;

public abstract class Filter implements Comparable<Filter> {

	/**
	 * The name of the filter.
	 */
	private final String name;

	/**
	 * The parameters of the filter.
	 */
	private final Parameters parameters;

	/**
	 * The log to filter. May change, hence not final.
	 */
	private XLog log;

	/**
	 * Current filter is valid for this log.
	 */
	private XLog valid4Log;

	/**
	 * The widget to change the parameters.
	 */
	private JComponent widget;

	private ComputationCell cell;

	private final XFactory factory;

	Filter(String name, Parameters parameters, ComputationCell cell) {
		this.name = name;
		this.parameters = parameters;
		this.cell = cell;
		factory = XFactoryRegistry.instance().currentDefault();
		valid4Log = null;
	}

	/**
	 * Gets the name of the filter.
	 * 
	 * @return The name of the filter.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the parameters of the filter.
	 * 
	 * @return The parameters of the filter.
	 */
	public Parameters getParameters() {
		return parameters;
	}

	/**
	 * Abstract method that filters the log using this filter and these parameters.
	 * 
	 * @return The filtered log.
	 */
	public abstract XLog filter();

	/**
	 * Abstract method that constructs a JComponent to enable the user to change the
	 * parameters.
	 */
	public abstract void constructWidget();

	/**
	 * Abstract method that informs the filter that a parameter has been changed.
	 * 
	 * @param parameter
	 *            The parameter that has been changed.
	 */
	public abstract void updated(Parameter parameter);

	public abstract boolean isSuitable();

	/**
	 * Filters the log while timing the filtering.
	 * 
	 * @param doTime
	 *            Whether to time the filtering.
	 * @return The filtered log.
	 */
	public XLog filter(boolean doTime) {
		long millis = 0L;
		if (doTime) {
			System.out.println("[Filter] " + name + ": started.");
			millis = -System.currentTimeMillis();
		}
		XLog filteredLog = filter();
		if (doTime) {
			millis += System.currentTimeMillis();
			System.out.println("[Filter] " + name + ": finished in " + millis + " milliseconds.");
		}
		return filteredLog;
	}

	/**
	 * Creates a copy of the provided log without any traces and/or events.
	 * 
	 * @param log
	 *            The log to copy.
	 * @return The copied log.
	 */
	public XLog initializeLog(XLog log) {
		XLog filteredLog = factory.createLog((XAttributeMap) log.getAttributes().clone());
		filteredLog.getExtensions().addAll(log.getExtensions());
		filteredLog.getGlobalTraceAttributes().addAll(log.getGlobalTraceAttributes());
		filteredLog.getGlobalEventAttributes().addAll(log.getGlobalEventAttributes());
		filteredLog.getClassifiers().addAll(log.getClassifiers());
		return filteredLog;
	}

	/**
	 * Gets the log that is to be filtered.
	 * 
	 * @return The log to be filtered.
	 */
	public XLog getLog() {
		return log;
	}

	/**
	 * Sets the log that is to be filtered.
	 * 
	 * @param log
	 *            The log to be filtered.
	 */
	public void setLog(XLog log) {
		if (this.log != log) {
			widget = null;
		}
		this.log = log;
	}

	/**
	 * Gets the widget to change the parameters.
	 * 
	 * @return The widget to change the parameters.
	 */
	public JComponent getWidget() {
		if (widget == null) {
			constructWidget();
		}
		return widget;
	}

	/**
	 * Sets the widget to change the parameters.
	 * 
	 * @param widget
	 *            The widget to change the parameters.
	 */
	public void setWidget(JComponent widget) {
		this.widget = widget;
	}

	public int compareTo(Filter o) {
		return name.compareTo(o.getName());
	}

	public String toString() {
		return name;
	}

	public ComputationCell getCell() {
		return cell;
	}

	public XFactory getFactory() {
		return factory;
	}

	public abstract void updateParameters();

	public void update() {
		if (getLog() != valid4Log) {
			/*
			 * Input log has changed.
			 */
			if (getLog() != null) {
				/*
				 * Update the parameters on the new input log.
				 */
				updateParameters();
				widget = null;
			}
			valid4Log = getLog();
		}
	}

	public void setSelected(Filter filter) {
		getCell().setSelectedFilter(filter);
	}
	
	public abstract FilterTemplate getTemplate();
	
	public abstract void setTemplate(ParametersTemplate parameters);
}