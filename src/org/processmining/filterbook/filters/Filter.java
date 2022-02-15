package org.processmining.filterbook.filters;

import java.util.Collection;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
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

	private static XAttribute dummyAttribute = null;
	private static XEventClassifier dummyClassifier = null;
	
	public Filter(String name, Parameters parameters, ComputationCell cell) {
		this.name = name;
		this.parameters = parameters;
		this.cell = cell;
		factory = XFactoryRegistry.instance().currentDefault();
		if (dummyAttribute == null) {
			dummyAttribute = factory.createAttributeLiteral("", "", null);
		}
		if (dummyClassifier == null) {
			dummyClassifier = new XEventAttributeClassifier("", "");
		}
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
//		System.out.println("[Filter] " + name + ": get, " + log.size() + " traces");
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
//		System.out.println("[Filter] " + name + ": set, " + log.size() + " traces");
		this.log = log;
	}

	/**
	 * Gets the widget to change the parameters.
	 * 
	 * @return The widget to change the parameters.
	 */
	public JComponent getWidget() {
		if (widget == null) {
//			System.out.println("[Filter] " + name + ": widget, " + log.size() + " traces");
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

	public boolean hasGlobalEventAttributes() {
		return !getLog().getGlobalEventAttributes().isEmpty();
	}
	
	public boolean hasGlobalTraceAttributes() {
		return !getLog().getGlobalTraceAttributes().isEmpty();
	}
	
	public boolean hasClassifiers() {
		return !getLog().getClassifiers().isEmpty();
	}
	
	public boolean hasEvents( ) {
		for (XTrace trace : getLog()) {
			if (!trace.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasTraces( ) {
		return !getLog().isEmpty();
	}
	
	public boolean hasTraceAttributes() {
		for (XTrace trace : getLog()) {
			if (!trace.getAttributes().isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasTimeExtension() {
		return getLog().getExtensions().contains(XTimeExtension.instance());
	}
	
	public boolean hasGlobalTimestamp() {
		for (XAttribute attribute : getLog().getGlobalEventAttributes()) {
			if (attribute.getKey().equals(XTimeExtension.KEY_TIMESTAMP)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasConceptExtension(XLog log) {
		return getLog().getExtensions().contains(XConceptExtension.instance());
	}
	
	public boolean hasGlobalConceptName(Collection<XAttribute> globalAttributes) {
		for (XAttribute globalAttribute : globalAttributes) {
			if (globalAttribute.getKey().equals(XConceptExtension.KEY_NAME)) {
				return true;
			}
		}
		return false;
	}

	public XAttribute getDummyAttribute() {
		return dummyAttribute;
	}

	public XEventClassifier getDummyClassifier() {
		return dummyClassifier;
	}

}
