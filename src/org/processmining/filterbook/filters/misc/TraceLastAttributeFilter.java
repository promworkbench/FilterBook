package org.processmining.filterbook.filters.misc;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.filters.Filter;
import org.processmining.filterbook.filters.FilterTemplate;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TraceLastAttributeFilter extends Filter {

	public static final String NAME = "Provide every trace with last event attributes";

	private XLog cachedLog;
	private XLog cachedFilteredLog;

	public TraceLastAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	public TraceLastAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasEvents();
	}

	public XLog filter() {
		/*
		 * Check whether the cache is valid.
		 */
		if (cachedLog == getLog()) {
			if (true) {
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
		Set<String> keys = new HashSet<String>();
		for (XTrace trace : getLog()) {
			keys.addAll(trace.getAttributes().keySet());
		}
		for (XTrace trace : getLog()) {
			XTrace filteredTrace = getFactory().createTrace((XAttributeMap) trace.getAttributes().clone());
			filteredTrace.addAll(trace);
			for (XEvent event : filteredTrace) {
				for (XAttribute attribute : event.getAttributes().values()) {
					if (!keys.contains(attribute.getKey())) {
						filteredTrace.getAttributes().put(attribute.getKey(), attribute);
					}
				}
			}
			filteredLog.add(filteredTrace);
		}		
		/*
		 * Update the cache and return the result.
		 */
		cachedLog = getLog();
		cachedFilteredLog = filteredLog;
		return filteredLog;
	}
	
	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));
		JLabel label = new JLabel("<html><h2>This filter has no configuration options</h2></html>");
		label.setHorizontalAlignment(JLabel.CENTER);
		widget.add(label, "0, 0");
		setWidget(widget);
	}

	public void updated(Parameter parameter) {
		getCell().updated();
	}

	public void updateParameters() {
	}

	public FilterTemplate getTemplate() {
		FilterTemplate filterTemplate = new FilterTemplate();
		filterTemplate.setName(getClass().getName());
		filterTemplate.setParameters(new ParametersTemplate());
		return filterTemplate;
	}

	public void setTemplate(ParametersTemplate parameters) {
	}
}
