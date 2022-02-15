package org.processmining.filterbook.filters.select.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class TraceVariantRandomGlobalAttributeFilter extends TraceVariantAbstractGlobalAttributeFilter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select random trace for every variant";

	public TraceVariantRandomGlobalAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public TraceVariantRandomGlobalAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	protected void select() {
		// TODO Auto-generated method stub
		Random random = new Random();
		if (selectedTraces == null) {
			selectedTraces = new HashMap<List<String>, List<XTrace>>();
			for (List<String> traceClass : traces.keySet()) {
				List<XTrace> traceList = new ArrayList<XTrace>();
				traceList.add(traces.get(traceClass).get(random.nextInt(traces.get(traceClass).size())));
				selectedTraces.put(traceClass, traceList);
			}
		}
	}

}

