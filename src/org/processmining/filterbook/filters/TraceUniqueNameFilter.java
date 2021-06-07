package org.processmining.filterbook.filters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TraceUniqueNameFilter extends Filter {

	public static final String NAME = "Ensure unique case names";

	public TraceUniqueNameFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
	}

	public TraceUniqueNameFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
	}

	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		if (!hasConceptExtension(getLog())) {
			/*
			 * This filter will add the concept extension.
			 */
			return true;
		}
		if (!hasGlobalConceptName(getLog().getGlobalTraceAttributes())) {
			/*
			 * This filter will add concept:name as global trace attribute.
			 */
			return true;
		}
		Set<String> conceptNames = new HashSet<String>();
		for (XTrace trace : getLog()) {
			if (!conceptNames.add(XConceptExtension.instance().extractName(trace))) {
				/*
				 * Non-unique concept:name. This fitler will make them unique.
				 */
				return true;
			}
		}
		/*
		 * Nothing to do for this filter.
		 */
		return false;
	}

	public XLog filter() {
		XLog filteredLog = initializeLog(getLog());
		/*
		 * Add the concept extension, if needed.
		 */
		if (!hasConceptExtension(getLog())) {
			filteredLog.getExtensions().add(XConceptExtension.instance());
		}
		/*
		 * Add concept:name as global trace attribute, if needed.
		 */
		if (!hasGlobalConceptName(getLog().getGlobalTraceAttributes())) {
			filteredLog.getGlobalTraceAttributes().add(XConceptExtension.ATTR_NAME);
		}
		/*
		 * Provide unique concept:name values for all traces.
		 */
		Map<String, Integer> conceptNames = new HashMap<String, Integer>();
		conceptNames.put("",  0);
		for (XTrace trace : getLog()) {
			XTrace filteredTrace = getFactory().createTrace(trace.getAttributes());
			filteredTrace.addAll(trace);
			String caseName = XConceptExtension.instance().extractName(trace);
			if (caseName == null) {
				caseName = "";
			}
			while (conceptNames.containsKey(caseName)) {
				/*
				 * Either we have seen this name before, or it is the first empty name.
				 * Increase the counter for this name.
				 */
				conceptNames.put(caseName, conceptNames.get(caseName) + 1);
				/*
				 * Append the counter to the name.
				 * The first empty name gets the name "1", the second "2", etc.
				 * The first second X gets the name "X 2", the third "X 3", etc.
				 */
				if (caseName == "") {
					caseName = "" + conceptNames.get(caseName);
				} else {
					caseName = caseName + " " + conceptNames.get(caseName);
				}
			}
			/*
			 * We now have a unique case name. Register and assign it.
			 */
			conceptNames.put(caseName,  1);
			XConceptExtension.instance().assignName(filteredTrace, caseName);
			filteredLog.add(filteredTrace);
		}		
		return filteredLog;
	}
	
	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));
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
