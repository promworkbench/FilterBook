package org.processmining.filterbook.filters.select.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class TraceVariantLastGlobalAttributeFilter extends TraceVariantAbstractGlobalAttributeFilter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select last trace for every variant";

	public TraceVariantLastGlobalAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public TraceVariantLastGlobalAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	public void select() {
		// TODO Auto-generated method stub
		if (selectedTraces == null) {
			selectedTraces = new HashMap<List<String>, List<XTrace>>();
			for (List<String> traceClass : traces.keySet()) {
				List<XTrace> traceList = new ArrayList<XTrace>();
				traceList.add(traces.get(traceClass).get(traces.get(traceClass).size() - 1));
				selectedTraces.put(traceClass, traceList);
			}
		}
	}

}

