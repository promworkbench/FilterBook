package org.processmining.filterbook.filters;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;
import org.processmining.filterbook.parameters.YesNoParameter;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class LogGlobalsFilter extends Filter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Provide log with global attributes";

	private XLog cachedLog;
	private boolean cachedYesNoA;
	private boolean cachedYesNoB;
	private XLog cachedFilteredLog;
	
	public LogGlobalsFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	public LogGlobalsFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	/**
	 * This filter is suitable if the log contains at least one event.
	 */
	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasEvents();
	}
	
	/**
	 * Filter the set log on the start events using the set parameters.
	 */
	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		boolean yesNoA = getParameters().getYesNoA().getSelected();
		boolean yesNoB = getParameters().getYesNoB().getSelected();
		/*
		 * Check whether the cache is  valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedYesNoA == yesNoA &&
					cachedYesNoB == yesNoB) {
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
		Set<String> traceKeys = null;
		Set<String> eventKeys = null;
		XTrace firstTrace = null;
		XEvent firstEvent = null;
		for (XTrace trace : getLog()) {
			if (yesNoA) {
				if (firstTrace == null) {
					firstTrace = trace;
				}
				if (traceKeys == null) {
					traceKeys = new HashSet<String>(trace.getAttributes().keySet());
				} else {
					traceKeys.retainAll(trace.getAttributes().keySet());
				}
			}
			if (yesNoB) {
				for (XEvent event : trace) {
					if (firstEvent == null) {
						firstEvent = event;
					}
					if (eventKeys == null) {
						eventKeys = new HashSet<String>(event.getAttributes().keySet());
					} else {
						eventKeys.retainAll(event.getAttributes().keySet());
					}
				}
			}
			filteredLog.add(trace);
		}
		if (yesNoA) {
			Set<String> globalTraceKeys = new HashSet<String>();
			for (XAttribute globalTraceAttribute : filteredLog.getGlobalTraceAttributes()) {
				globalTraceKeys.add(globalTraceAttribute.getKey());
			}
			traceKeys.removeAll(globalTraceKeys);
			for (String key : traceKeys) {
				filteredLog.getGlobalTraceAttributes().add(firstTrace.getAttributes().get(key));
			}
		}
		if (yesNoB) {
			Set<String> globalEventKeys = new HashSet<String>();
			for (XAttribute globalEventAttribute : filteredLog.getGlobalEventAttributes()) {
				globalEventKeys.add(globalEventAttribute.getKey());
			}
			eventKeys.removeAll(globalEventKeys);
			for (String key : eventKeys) {
				filteredLog.getGlobalEventAttributes().add(firstEvent.getAttributes().get(key));
			}
		}
		/*
		 * Update the cache and return the result.
		 */
		cachedLog = getLog();
		cachedYesNoA = yesNoA;
		cachedYesNoB = yesNoB;
		cachedFilteredLog = filteredLog;
		return filteredLog;
	}

	/**
	 * Construct a widget for changing the required parameters.
	 */
	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL }, { 30, 30, TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));
		widget.add(getParameters().getYesNoA().getWidget(), "0, 0");
		widget.add(getParameters().getYesNoB().getWidget(), "0, 1");
		setWidget(widget);
	}

	/**
	 * Handle if a parameter values was changed.
	 */
	public void updated(Parameter parameter) {
		getCell().updated();
	}

	private void setTraceGlobals(boolean doReset) {
		if (!doReset && getParameters().getYesNoA() != null) {
			return;
		}
		boolean selected = false;
		if (getParameters().getYesNoA() != null) {
			selected = getParameters().getYesNoA().getSelected();
		}
		getParameters().setYesNoA(new YesNoParameter("Add global trace attributes?", this, selected));
	}

	private void setEventGlobals(boolean doReset) {
		if (!doReset && getParameters().getYesNoB() != null) {
			return;
		}
		boolean selected = false;
		if (getParameters().getYesNoB() != null) {
			selected = getParameters().getYesNoB().getSelected();
		}
		getParameters().setYesNoB(new YesNoParameter("Add global event attributes?", this, selected));
	}

	/**
	 * Update the filter parameters.
	 */
	public void updateParameters() {
		setTraceGlobals(true);
		setEventGlobals(true);
	}
	
	public FilterTemplate getTemplate() {
		FilterTemplate filterTemplate = new FilterTemplate();
		filterTemplate.setName(getClass().getName());
		filterTemplate.setParameters(new ParametersTemplate());
		filterTemplate.getParameters().setYesNoA(getParameters().getYesNoA().getSelected());
		filterTemplate.getParameters().setYesNoB(getParameters().getYesNoB().getSelected());
		return filterTemplate;
	}

	public void setTemplate(ParametersTemplate parameters) {
		setTraceGlobals(true);
		getParameters().getYesNoA().setSelected(parameters.isYesNoA());
		setEventGlobals(true);
		getParameters().getYesNoB().setSelected(parameters.isYesNoB());
	}
}
