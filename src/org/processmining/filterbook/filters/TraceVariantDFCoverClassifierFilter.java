package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.charts.DirectlyFollowsChart;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.framework.util.Pair;
import org.processmining.lpengines.factories.LPEngineFactory;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.lpengines.interfaces.LPEngine.EngineType;
import org.processmining.lpengines.interfaces.LPEngine.ObjectiveTargetType;
import org.processmining.lpengines.interfaces.LPEngine.Operator;

public class TraceVariantDFCoverClassifierFilter extends TraceVariantAbstractClassifierFilter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select minimal set of traces that contain all direclty-follow relations";

	public TraceVariantDFCoverClassifierFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
		// TODO Auto-generated constructor stub
	}

	public TraceVariantDFCoverClassifierFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
		// TODO Auto-generated constructor stub
	}

	protected void select() {
		// TODO Auto-generated method stub
		if (selectedTraces == null) {
			Map<Pair<String, String>, Set<XTrace>> dfTraceMap = new HashMap<Pair<String, String>, Set<XTrace>>();
			for (List<String> traceClass : traces.keySet()) {
				String source = null;
				for (String target : traceClass) {
					if (source != null) {
						Pair<String, String> df = new Pair<String, String>(source, target);
						if (!dfTraceMap.containsKey(df)) {
							dfTraceMap.put(df, new HashSet<XTrace>());
						}
						dfTraceMap.get(df).add(traces.get(traceClass).get(0));
					}
					source = target;
				}
			}
			Map<XTrace, Integer> traceIdxMap = new HashMap<XTrace, Integer>();
			/*
			 * Create engine.
			 */
			LPEngine engine = LPEngineFactory.createLPEngine(EngineType.LPSOLVE, 0, 0);
			/*
			 * Add a variable for every trace, and set the objective to minimize
			 * the number of selected traces.
			 */
			Map<Integer, Double> objective = new HashMap<Integer, Double>();
			for (XTrace trace : getLog()) {
				traceIdxMap.put(trace,
						engine.addVariable(new HashMap<Integer, Double>(), LPEngine.VariableType.INTEGER));
				objective.put(traceIdxMap.get(trace), 1.0);
			}
			engine.setObjective(objective, ObjectiveTargetType.MIN);
			/*
			 * Set the constraints: Every df pair should be covered by at least
			 * one selected trace.
			 */
			for (Pair<String, String> df : dfTraceMap.keySet()) {
				/*
				 * At least one trace that contains the pair df should be
				 * included.
				 */
				Map<Integer, Double> constraint = new HashMap<Integer, Double>();
				for (XTrace trace : getLog()) {
					constraint.put(traceIdxMap.get(trace), dfTraceMap.get(df).contains(trace) ? 1.0 : 0.0);
				}
				engine.addConstraint(constraint, Operator.GREATER_EQUAL, 1.0);
			}
			/*
			 * Including all traces has to be a solution, so it should be
			 * solvable.
			 */
			Map<Integer, Double> solution = engine.solve();
			/*
			 * Create the collection of selected traces.
			 */
			selectedTraces = new HashMap<List<String>, List<XTrace>>();
			for (List<String> traceClass : traces.keySet()) {
				List<XTrace> traceList = new ArrayList<XTrace>();
				XTrace trace = traces.get(traceClass).get(0);
				if (solution.containsKey(traceIdxMap.get(trace)) && solution.get(traceIdxMap.get(trace)) > 0.0) {
					traceList.add(trace);
				}
				selectedTraces.put(traceClass, traceList);
			}
		}
	}

	protected JComponent getChartWidget() {
		return DirectlyFollowsChart.getChart(getLog(), getDummyClassifier(), getParameters());
	}
}
