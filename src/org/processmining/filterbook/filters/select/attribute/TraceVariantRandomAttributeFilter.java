package org.processmining.filterbook.filters.select.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class TraceVariantRandomAttributeFilter extends TraceVariantAbstractAttributeFilter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select first trace for every variant";

	public TraceVariantRandomAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public TraceVariantRandomAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	public void select() {
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
