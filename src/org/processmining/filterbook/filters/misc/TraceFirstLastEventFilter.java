package org.processmining.filterbook.filters.misc;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.charts.FirstLastChart;
import org.processmining.filterbook.filters.Filter;
import org.processmining.filterbook.filters.FilterTemplate;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;
import org.processmining.filterbook.parameters.YesNoParameter;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TraceFirstLastEventFilter extends Filter {

	public static final String NAME = "Provide every trace with artificial first and/or last event";

	private JComponent chartWidget;

	private XLog cachedLog;
	private boolean cachedYesNoA;
	private boolean cachedYesNoB;
	private XLog cachedFilteredLog;
	
	public TraceFirstLastEventFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	public TraceFirstLastEventFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasTraces();
	}

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
		for (XTrace trace : getLog()) {
			XTrace filteredTrace = getFactory().createTrace(trace.getAttributes());
			if (yesNoA) {
				XEvent firstEvent = getFactory().createEvent();
				boolean hasTimestamp = false;
				for (XAttribute globalAttribute : getLog().getGlobalEventAttributes()) {
					firstEvent.getAttributes().put(globalAttribute.getKey(), globalAttribute);
					if (globalAttribute.getKey().equals(XTimeExtension.KEY_TIMESTAMP)) {
						hasTimestamp = true;
					}
				}
				XConceptExtension.instance().assignName(firstEvent, "\u03b1");
				if (hasTimestamp) {
					if (trace.size() > 0) {
						XTimeExtension.instance().assignTimestamp(firstEvent, XTimeExtension.instance().extractTimestamp(trace.get(0)));
					}
				}
				filteredTrace.add(firstEvent);
			}
			filteredTrace.addAll(trace);
			if (yesNoB) {
				XEvent lastEvent = getFactory().createEvent();
				boolean hasTimestamp = false;
				for (XAttribute globalAttribute : getLog().getGlobalEventAttributes()) {
					lastEvent.getAttributes().put(globalAttribute.getKey(), globalAttribute);
					if (globalAttribute.getKey().equals(XTimeExtension.KEY_TIMESTAMP)) {
						hasTimestamp = true;
					}
				}
				XConceptExtension.instance().assignName(lastEvent, "\u03c9");
				if (hasTimestamp) {
					if (trace.size() > 0) {
						XTimeExtension.instance().assignTimestamp(lastEvent, XTimeExtension.instance().extractTimestamp(trace.get(trace.size() - 1)));
					}
				}
				filteredTrace.add(lastEvent);
			}
			filteredLog.add(filteredTrace);
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

	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL }, { 30, 30, TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));
		widget.add(getParameters().getYesNoA().getWidget(), "0, 0");
		widget.add(getParameters().getYesNoB().getWidget(), "0, 1");
		chartWidget = getChartWidget();
		widget.add(chartWidget, "1, 0, 1, 2");
		setWidget(widget);
	}

	public JComponent getChartWidget() {
		return FirstLastChart.getChart(getLog());
	}

	public void updated(Parameter parameter) {
		getCell().updated();
	}

	private void setFirst(boolean doReset) {
		if (!doReset && getParameters().getYesNoA() != null) {
			return;
		}
		boolean selected = false;
		if (getParameters().getYesNoA() != null) {
			selected = getParameters().getYesNoA().getSelected();
		}
		getParameters().setYesNoA(new YesNoParameter("Add artificial first \u03b1 event?", this, selected));
	}

	private void setLast(boolean doReset) {
		if (!doReset && getParameters().getYesNoB() != null) {
			return;
		}
		boolean selected = false;
		if (getParameters().getYesNoB() != null) {
			selected = getParameters().getYesNoB().getSelected();
		}
		getParameters().setYesNoB(new YesNoParameter("Add artifical last \u03c9 event?", this, selected));
	}

	public void updateParameters() {
		setFirst(true);
		setLast(true);
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
		setFirst(true);
		getParameters().getYesNoA().setSelected(parameters.isYesNoA());
		setLast(true);
		getParameters().getYesNoB().setSelected(parameters.isYesNoB());
	}
}
