package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class TraceVariantFirstGlobalAttributeFilter extends TraceVariantAbstractGlobalAttributeFilter{

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select first variant using global attribute value";
	
	public TraceVariantFirstGlobalAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public TraceVariantFirstGlobalAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	protected void select() {
		// TODO Auto-generated method stub
		Map<List<String>, List<XTrace>> selectedTraces = new HashMap<List<String>, List<XTrace>>();
		for (List<String> traceClass : traces.keySet()) {
			List<XTrace> traceList = new ArrayList<XTrace>();
			traceList.add(traces.get(traceClass).get(0));
			selectedTraces.put(traceClass, traceList);
		}
		traces = selectedTraces;
	}

}
