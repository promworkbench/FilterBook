package org.processmining.filterbook.filters;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
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

public class TraceFirstLastEventFilter extends Filter {

	public static final String NAME = "Add artificial first and/or last";

	public TraceFirstLastEventFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
	}

	public TraceFirstLastEventFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
	}

	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasTraces();
	}

	public XLog filter() {
		XLog filteredLog = initializeLog(getLog());
		for (XTrace trace : getLog()) {
			XTrace filteredTrace = getFactory().createTrace(trace.getAttributes());
			if (getParameters().getYesNoA().getSelected()) {
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
			if (getParameters().getYesNoB().getSelected()) {
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
		return filteredLog;
	}

	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL }, { 30, 30, TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));
		widget.add(getParameters().getYesNoA().getWidget(), "0, 0");
		widget.add(getParameters().getYesNoB().getWidget(), "0, 1");
		setWidget(widget);
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
		getParameters().setYesNoA(new YesNoParameter("Add first \u03b1 event?", this, selected));
	}

	private void setLast(boolean doReset) {
		if (!doReset && getParameters().getYesNoB() != null) {
			return;
		}
		boolean selected = false;
		if (getParameters().getYesNoB() != null) {
			selected = getParameters().getYesNoB().getSelected();
		}
		getParameters().setYesNoB(new YesNoParameter("Add last \u03c9 event?", this, selected));
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
