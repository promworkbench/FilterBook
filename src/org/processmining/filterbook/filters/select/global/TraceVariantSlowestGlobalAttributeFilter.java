package org.processmining.filterbook.filters.select.global;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public class TraceVariantSlowestGlobalAttributeFilter extends TraceVariantTimeAbstractGlobalAttributeFilter  {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select slowest trace for every variant";

	public TraceVariantSlowestGlobalAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public TraceVariantSlowestGlobalAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	protected void select() {
		// TODO Auto-generated method stub
		if (selectedTraces == null) {
			selectedTraces = new HashMap<List<String>, List<XTrace>>();
			for (List<String> traceClass : traces.keySet()) {
				List<XTrace> traceList = new ArrayList<XTrace>();
				int selectedIdx = -1;
				long selectedDuration = 0;
				for (int i = 0; i < traces.get(traceClass).size(); i++) {
					XTrace trace = traces.get(traceClass).get(i);
					Date firstDate = XTimeExtension.instance().extractTimestamp(trace.get(0));
					Date lastDate = XTimeExtension.instance().extractTimestamp(trace.get(trace.size() - 1));
					long duration = lastDate.getTime() - firstDate.getTime();
					if (selectedIdx < 0 || duration > selectedDuration) {
						selectedIdx = i;
						selectedDuration = duration;
					}
				}
				traceList.add(traces.get(traceClass).get(selectedIdx));
				selectedTraces.put(traceClass, traceList);
			}
		}
	}
}
