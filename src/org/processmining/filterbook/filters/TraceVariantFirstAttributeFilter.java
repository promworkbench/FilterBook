package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class TraceVariantFirstAttributeFilter extends TraceVariantAbstractAttributeFilter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select first variant using attribute value";

	public TraceVariantFirstAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public TraceVariantFirstAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
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
